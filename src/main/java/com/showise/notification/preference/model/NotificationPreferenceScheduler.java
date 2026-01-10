package com.showise.notification.preference.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.showise.message.model.MailService;

@Component
@ConditionalOnProperty(name="noti.pref.scheduler.enabled", havingValue="true", matchIfMissing = false)
public class NotificationPreferenceScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationPreferenceScheduler.class);

    // ✅ 狀態碼請全專案統一
    private static final short STAT_SENT    = 0; // 已寄
    private static final short STAT_PENDING = 1; // 待寄
    private static final short STAT_SENDING = 2; // 寄送中（防重寄）
    private static final short STAT_FAILED  = 9; // 失敗

    private final NotificationPreferenceRepository repo;
    private final MailService mailService;

    public NotificationPreferenceScheduler(NotificationPreferenceRepository repo,
                                           MailService mailService) {
        this.repo = repo;
        this.mailService = mailService;
    }

    @Transactional
    @Scheduled(cron = "0 */1 * * * *", zone = "Asia/Taipei")
    public void sendDueMails() {

        LocalDate today = LocalDate.now();

        // ✅ 只掃「待寄」且 stime <= today（LocalDate 對 LocalDate）
        List<NotificationPreferenceVO> dueList =
                repo.findByNotiPrefStatAndNotiPrefStimeLessThanEqual(STAT_PENDING, today);

        if (dueList == null || dueList.isEmpty()) return;

        log.info("[NotificationPreferenceScheduler] dueList size={}", dueList.size());

        for (NotificationPreferenceVO vo : dueList) {
            String toEmail = null;

            try {
                // --- 基本資料檢查 ---
                if (vo.getMember() == null
                        || vo.getMember().getEmail() == null
                        || vo.getMember().getEmail().isBlank()) {

                    log.warn("[NotificationPreferenceScheduler] skip: member/email is null, notiPrefNo={}", vo.getNotiPrefNo());
                    vo.setNotiPrefStat(STAT_FAILED);
                    repo.save(vo);
                    continue;
                }

                toEmail = vo.getMember().getEmail().trim();
                String content = (vo.getNotiPrefScon() == null) ? "" : vo.getNotiPrefScon().trim();

                if (content.isBlank()) {
                    log.warn("[NotificationPreferenceScheduler] skip: content is blank, notiPrefNo={}", vo.getNotiPrefNo());
                    vo.setNotiPrefStat(STAT_FAILED);
                    repo.save(vo);
                    continue;
                }

                // ✅ 先標記寄送中（防止同一批/多執行緒重複寄）
                vo.setNotiPrefStat(STAT_SENDING);
                repo.save(vo);

                // --- 寄信 ---
                mailService.sendTextMail(toEmail, "喜好通知", content);

                // ✅ 成功 → 已寄
                vo.setNotiPrefStat(STAT_SENT);
                repo.save(vo);

                log.info("[NotificationPreferenceScheduler] sent ok, notiPrefNo={}, to={}", vo.getNotiPrefNo(), toEmail);

            } catch (Exception e) {

                log.error("[NotificationPreferenceScheduler] sent failed, notiPrefNo={}, to={}, err={}",
                        vo.getNotiPrefNo(), toEmail, e.getMessage(), e);

                // ✅ 失敗就標記 9，避免重寄
                try {
                    vo.setNotiPrefStat(STAT_FAILED);
                    repo.save(vo);
                } catch (Exception ex) {
                    log.error("[NotificationPreferenceScheduler] update STAT_FAILED failed, notiPrefNo={}, err={}",
                            vo.getNotiPrefNo(), ex.getMessage(), ex);
                }
            }
        }
    }
}
