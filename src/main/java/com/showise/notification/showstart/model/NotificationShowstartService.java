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

	public void deleteNotificationShowstart(Integer notiShowstNo) {
		if (repository.existsById(notiShowstNo))
			repository.deleteBynotiShowstNo(notiShowstNo);
	}

	public NotificationShowstartVO getOneNotificationShowstart(Integer notiShowstNo) {
		Optional<NotificationShowstartVO> optional = repository.findById(notiShowstNo);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<NotificationShowstartVO> getAll() {
		return repository.findAll();
	}

	public List<NotificationShowstartVO> getAll(Map<String, String[]> map) {
		return repository.findAll();
	}

}