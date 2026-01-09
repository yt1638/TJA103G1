package com.showise.notification.showstart.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationShowstartRepository extends JpaRepository<NotificationShowstartVO, Integer> {

    // ✅ 複合查詢：memberId / sessionId / stime(日期) 可空
    // ✅ 排序：先會員編號 ASC，再通知時間 ASC（最新在最下面）
    @Query(value = """
        select *
        from notification_showstart
        where (:memberId is null or member_id = :memberId)
          and (:sessionId is null or session_id = :sessionId)
          and (:stime is null or date(noti_showstSTIME) = :stime)
        order by member_id asc, noti_showstSTIME asc
        """, nativeQuery = true)
    List<NotificationShowstartVO> compositeQuery(@Param("memberId") Integer memberId,
                                                 @Param("sessionId") Integer sessionId,
                                                 @Param("stime") Date stime);

    // ✅ Scheduler 用：待寄 + 到期
    @Query(value = """
        select *
        from notification_showstart
        where noti_showstSTAT = :pending
          and noti_showstSTIME <= :now
        order by member_id asc, noti_showstSTIME asc
        """, nativeQuery = true)
    List<NotificationShowstartVO> findDueToSend(@Param("pending") short pending,
                                                @Param("now") Timestamp now);

    // ✅ 直接查 email（避開 lazy / 關聯）
    @Query(value = "select email from member where member_id = :memberId", nativeQuery = true)
    String findMemberEmail(@Param("memberId") Integer memberId);

    // ✅ 更新狀態
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        update notification_showstart
        set noti_showstSTAT = :stat
        where noti_showstNO = :no
        """, nativeQuery = true)
    int updateStat(@Param("no") Integer notiShowstNo,
                   @Param("stat") short stat);
}
