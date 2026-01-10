package com.showise.notification.showstart.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.showise.message.model.MailService;

@Component
public class NotificationShowstartScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationShowstartScheduler.class);

    private final NotificationShowstartRepository repo;
    private final MailService mailService;

    public NotificationShowstartScheduler(NotificationShowstartRepository repo,
                                          MailService mailService) {
        this.repo = repo;
        this.mailService = mailService;
    }

    @Transactional
    @Scheduled(cron = "0 */1 * * * *", zone = "Asia/Taipei") // 每分鐘跑一次
    public void sendDueShowstart() {

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        // ✅ 1=待寄，且 stime <= now
        List<NotificationShowstartVO> dueList =
                repo.findDueToSend(NotificationShowstartService.STAT_PENDING, now);

        if (dueList == null || dueList.isEmpty()) return;

        log.info("[ShowstartScheduler] dueList size={}", dueList.size());

        for (NotificationShowstartVO n : dueList) {
            Integer notiNo = n.getNotiShowstNo();

            try {
                // ⚠️ 不要碰 n.getSession()
                // member_id 從關聯拿 id（通常不會觸發 join session）
                Integer memberId = (n.getMember() != null) ? n.getMember().getMemberId() : null;
                if (memberId == null) {
                    log.warn("[ShowstartScheduler] skip: memberId null, notiShowstNo={}", notiNo);
                    repo.updateStat(notiNo, NotificationShowstartService.STAT_FAILED);
                    continue;
                }

                String email = repo.findMemberEmail(memberId);
                if (email == null || email.isBlank()) {
                    log.warn("[ShowstartScheduler] skip: email blank, notiShowstNo={}", notiNo);
                    repo.updateStat(notiNo, NotificationShowstartService.STAT_FAILED);
                    continue;
                }

                String content = (n.getNotiShowstScon() == null) ? "" : n.getNotiShowstScon().trim();
                if (content.isBlank()) {
                    log.warn("[ShowstartScheduler] skip: content blank, notiShowstNo={}", notiNo);
                    repo.updateStat(notiNo, NotificationShowstartService.STAT_FAILED);
                    continue;
                }

                // ✅ 寄信
                mailService.sendTextMail(email.trim(), "開演通知", content);

                // ✅ 0=已寄（依你定義）
                repo.updateStat(notiNo, NotificationShowstartService.STAT_SENT);

                log.info("[ShowstartScheduler] SENT notiShowstNo={} to={}", notiNo, email);

            } catch (Exception ex) {
                log.error("[ShowstartScheduler] FAILED notiShowstNo={}, err={}", notiNo, ex.getMessage(), ex);
                repo.updateStat(notiNo, NotificationShowstartService.STAT_FAILED);
            }
        }
    }
}
