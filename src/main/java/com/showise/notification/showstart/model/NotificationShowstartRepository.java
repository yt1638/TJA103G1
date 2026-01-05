package com.showise.notification.showstart.model;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationShowstartRepository extends JpaRepository<NotificationShowstartVO, Integer> {

	@Query("""
			select n
			from NotificationShowstartVO n
			where (:memberId is null or n.member.memberId = :memberId)
			  and (:sessionId is null or n.session.sessionId = :sessionId)
			  and (:sendDate is null or function('date', n.notiShowstStime) = :sendDate)
			""")
			List<NotificationShowstartVO> compositeQuery(Integer memberId, Integer sessionId, java.sql.Date sendDate);
}
