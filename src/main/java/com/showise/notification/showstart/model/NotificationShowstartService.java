package com.showise.notification.showstart.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("notification_showstart")
public class NotificationShowstartService {

	@Autowired
	NotificationShowstartRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addNotificationShowstart(NotificationShowstartVO notificationShowstartVO) {
		repository.save(notificationShowstartVO);
	}

	public void updateNotificationShowstart(NotificationShowstartVO notificationShowstartVO) {
		repository.save(notificationShowstartVO);
	}


	public NotificationShowstartVO getOneNotificationShowstart(Integer notiShowstNo) {
		Optional<NotificationShowstartVO> optional = repository.findById(notiShowstNo);
		return optional.orElse(null);  
	}

	public List<NotificationShowstartVO> getAll() {
		return repository.findAll();
	}

	public List<NotificationShowstartVO> getAll(Map<String, String[]> map) {
		return repository.findAll();
	}

}