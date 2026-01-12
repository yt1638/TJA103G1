package com.showise.notification.preference.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.showise.member.model.MemberVO;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreferenceVO, Integer> {

    @Query("""
        select n
        from NotificationPreferenceVO n
        where (:memberId is null or n.member.memberId = :memberId)
          and (:movieId  is null or n.movie.movieId   = :movieId)
          and (:sendDate is null or function('date', n.notiPrefStime) = :sendDate)
        """)
    List<NotificationPreferenceVO> compositeQuery(
            @Param("memberId") Integer memberId,
            @Param("movieId")  Integer movieId,
            @Param("sendDate") java.util.Date sendDate
    );
    
    // Service 用：指定日期 + 狀態
    List<NotificationPreferenceVO> findByNotiPrefStimeAndNotiPrefStat(LocalDate notiPrefStime, Short notiPrefStat);

    // Scheduler 用：狀態 + 日期<=today
    List<NotificationPreferenceVO> findByNotiPrefStatAndNotiPrefStimeLessThanEqual(Short notiPrefStat, LocalDate notiPrefStime);

}

