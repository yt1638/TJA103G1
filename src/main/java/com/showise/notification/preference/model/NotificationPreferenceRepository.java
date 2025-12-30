package com.showise.notification.preference.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreferenceVO, Integer> {


	@Query("""
		    from NotificationPreferenceVO n
		    where n.notiPrefNo = ?1
		      and n.movie.movieId = ?2
		      and n.member.memberId = ?3
		    order by n.notiPrefNo
		""")
    List<NotificationPreferenceVO> findByOthers(int notiPrefNo, int movieId, int memberId);
}
