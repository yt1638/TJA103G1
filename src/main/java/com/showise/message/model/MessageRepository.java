package com.showise.message.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.showise.notification.preference.model.NotificationPreferenceVO;

public interface MessageRepository extends JpaRepository<MessageVO, Integer> {
	@Query("from MessageVO where msgNo = ?1 and msgCont = ?2 and msgTime = ?3 order by msgNo")
    List<MessageVO> findByOthers(int msgNo, int msgCont, int msgTime);
}
