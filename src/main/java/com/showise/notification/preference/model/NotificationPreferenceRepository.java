package com.showise.notification.preference.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreferenceVO, Integer> {


    // 自訂條件查詢（JPQL：用 Entity 屬性名）
    @Query("from NotificationPreferenceVO where notiPrefNo = ?1 and movieId = ?2 and memberId = ?3 order by notiPrefNo")
    List<NotificationPreferenceVO> findByOthers(int notiPrefNo, int movieId, int memberId);
}
