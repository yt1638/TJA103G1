package com.showise.notification.preference.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("notification_preference")
public class NotificationPreferenceService {

	@Autowired
	NotificationPreferenceRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addNotificationPreference(NotificationPreferenceVO notificationPreferenceVO) {
		repository.save(notificationPreferenceVO);
	}

	public void updateNotificationPreference(NotificationPreferenceVO notificationPreferenceVO) {
		repository.save(notificationPreferenceVO);
	}


	public NotificationPreferenceVO getOneNotificationPreference(Integer notiPrefNo) {
		Optional<NotificationPreferenceVO> optional = repository.findById(notiPrefNo);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<NotificationPreferenceVO> getAll() {
		return repository.findAll();
	}

	public List<NotificationPreferenceVO> getAll(Map<String, String[]> map) {
		return repository.findAll();
	}

}