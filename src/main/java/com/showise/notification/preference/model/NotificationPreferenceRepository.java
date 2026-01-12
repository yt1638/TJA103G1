package com.showise.notification.preference.model;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
