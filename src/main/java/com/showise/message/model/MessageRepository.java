package com.showise.message.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface MessageRepository extends JpaRepository<MessageVO, Integer> {
	
	
	@Query("from MessageVO where msgType = :msgType")
	MessageVO findByType(Integer msgType);
}
