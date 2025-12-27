package com.showise.notification.showstart.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationShowstartRepository extends JpaRepository<NotificationShowstartVO, Integer> {

    // JPQL：用 Entity 屬性名 + Integer 用 =
	@Query("""
			from NotificationShowstartVO n
			where n.notiShowstNo = ?1
			  and n.member.memberId = ?2
			  and n.session.sessionId = ?3
			order by n.notiShowstNo
			""")
    List<NotificationShowstartVO> findByOthers(int notiShowstNo, int memberId, int sessionId);
}
