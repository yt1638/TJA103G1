package com.showise.notification.showstart.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.showise.member.model.MemberRepository;
import com.showise.member.model.MemberVO;
import com.showise.session.model.SessionVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class NotificationShowstartService {

    // ✅ 狀態碼：0=已寄、1=待寄、9=失敗
    public static final short STAT_SENT    = 0;
    public static final short STAT_PENDING = 1;
    public static final short STAT_FAILED  = 9;

    @Autowired
    private NotificationShowstartRepository repo;

    @Autowired
    private MemberRepository memberRepo;

    // ✅ 不走 SessionRepository.findById()（會因 SessionVO 映射到不存在的 session_status 而爆）
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    // ===================== 基本 CRUD =====================

    public NotificationShowstartVO save(NotificationShowstartVO vo) {
        return repo.save(vo);
    }

    public MemberVO getMember(Integer memberId) {
        return memberRepo.findById(memberId).orElse(null);
    }

    // ❌ 不建議再提供 getSession/findById（會爆），直接給安全替代法：
    // 1) 只查 start_time：getSessionStartTime()
    // 2) 只當 FK：getSessionRef()

    // ✅ 只留一個：你的 MemberVO 是 getEmail()（你前端也用 m.email）
    public String getMemberEmail(Integer memberId) {
        if (memberId == null) return null;
        return memberRepo.findById(memberId)
                .map(MemberVO::getEmail)
                .orElse(null);
    }

    public List<NotificationShowstartVO> compositeQuery(Integer memberId, Integer sessionId, java.sql.Date stime) {
        return repo.compositeQuery(memberId, sessionId, stime);
    }

    // ===================== Session 安全替代（不碰 SessionVO 欄位映射） =====================

    /**
     * ✅ 直接用原生 SQL 讀取 session.start_time，避免 Hibernate SELECT 出 session_status。
     * 你的表名是 session（MySQL 保留字），保險起見加反引號。
     */
    public Timestamp getSessionStartTime(Integer sessionId) {
        if (sessionId == null) return null;
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT start_time FROM `session` WHERE session_id = ?",
                    Timestamp.class,
                    sessionId
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /** ✅ 檢查 session 是否存在（避免亂輸入 sessionId） */
    public boolean existsSession(Integer sessionId) {
        if (sessionId == null) return false;
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM `session` WHERE session_id = ?",
                Integer.class,
                sessionId
        );
        return cnt != null && cnt > 0;
    }

    /**
     * ✅ 只拿 reference 當 FK 用，不會發 SELECT（除非你去讀它其他欄位）。
     * 用來 vo.setSession(...) 很適合。
     */
    public SessionVO getSessionRef(Integer sessionId) {
        if (sessionId == null) return null;
        return entityManager.getReference(SessionVO.class, sessionId);
    }

    // ===================== 立即寄信：寄完後回存一筆(0=已寄) =====================

    @Transactional
    public NotificationShowstartVO logSentNow(Integer memberId,
                                             Integer sessionId,
                                             String content,
                                             Timestamp sentAt) {

        if (memberId == null) throw new IllegalArgumentException("memberId 不可為空");
        if (sessionId == null) throw new IllegalArgumentException("sessionId 不可為空");

        if (sentAt == null) sentAt = Timestamp.valueOf(LocalDateTime.now());

        MemberVO member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("memberId 不存在: " + memberId));

        // ✅ 不用 sessionRepo.findById()，改成 exists + getReference
        if (!existsSession(sessionId)) {
            throw new IllegalArgumentException("sessionId 不存在: " + sessionId);
        }
        SessionVO sessionRef = getSessionRef(sessionId);

        NotificationShowstartVO vo = new NotificationShowstartVO();
        vo.setMember(member);
        vo.setSession(sessionRef);
        vo.setNotiShowstScon(content);
        vo.setNotiShowstStime(sentAt);
        vo.setNotiShowstStat(STAT_SENT);

        return repo.save(vo);
    }

    // ===================== 排程：存一筆(1=待寄)，時間 = 開演時間 - advanceHours =====================

    @Transactional
    public Timestamp schedule(Integer memberId,
                              Integer sessionId,
                              String content,
                              int advanceHours) {

        if (memberId == null) throw new IllegalArgumentException("memberId 不可為空");
        if (sessionId == null) throw new IllegalArgumentException("sessionId 不可為空");
        if (advanceHours <= 0) throw new IllegalArgumentException("advanceHours 必須 > 0");

        MemberVO member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("memberId 不存在: " + memberId));

        // ✅ 不讀 SessionVO，直接查 start_time
        Timestamp sessionStart = getSessionStartTime(sessionId);
        if (sessionStart == null) {
            throw new IllegalArgumentException("找不到場次或開演時間為空，sessionId=" + sessionId);
        }

        Timestamp sendAt = Timestamp.valueOf(sessionStart.toLocalDateTime().minusHours(advanceHours));

        // ✅ 存 FK 用 reference，不查表
        SessionVO sessionRef = getSessionRef(sessionId);

        NotificationShowstartVO vo = new NotificationShowstartVO();
        vo.setMember(member);
        vo.setSession(sessionRef);
        vo.setNotiShowstScon(content);
        vo.setNotiShowstStime(sendAt);
        vo.setNotiShowstStat(STAT_PENDING);

        repo.save(vo);
        return sendAt;
    }

    // ===================== Scheduler：抓 due（1=待寄、stime<=now） =====================

    public List<NotificationShowstartVO> findDueToSend(Timestamp now) {
        if (now == null) now = Timestamp.valueOf(LocalDateTime.now());

        // ✅ 這行取決於你 Repository 的參數順序
        return repo.findDueToSend(STAT_PENDING, now);

        // 若你的 repo 是版本B：findDueToSend(Timestamp now, short pending)
        // 請改成：return repo.findDueToSend(now, STAT_PENDING);
    }
}
