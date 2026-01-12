package com.showise.message.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.showise.notification.preference.model.NotificationPreferenceVO;



@Service("message")
public class MessageService {

	@Autowired
	MessageRepository repository;
	
	public MessageVO getOneById(Integer msgNo) {
		Optional<MessageVO> optional = repository.findById(msgNo);
		return optional.orElse(null);  
	}

	public List<MessageVO> getAll() {
		return repository.findAll();
	}
	
	public List<MessageVO> getAll(Map<String, String[]> map) {
		return repository.findAll();
	}
	
	public void updateMessage (MessageVO messageVO) {
		repository.save(messageVO);
	}
	
	public MessageVO findByType(Integer type) {
		return repository.findByType(type);
	}
}