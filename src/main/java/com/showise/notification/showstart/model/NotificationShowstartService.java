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

	public List<NotificationShowstartVO> compositeQuery(Map<String, String> map) {

	    Integer memberId  = parseIntOrNull(map.get("memberId"));
	    Integer sessionId = parseIntOrNull(map.get("sessionId"));
	    java.sql.Date stime = parseDateOrNull(map.get("notiShowstStime"));

	    return repository.compositeQuery(memberId, sessionId, stime);
	}

	private String getFirst(Map<String, String[]> map, String key) {
	    String[] arr = map.get(key);
	    if (arr == null || arr.length == 0) return null;
	    String v = arr[0];
	    return (v == null || v.trim().isEmpty()) ? null : v.trim();
	}

	private Integer parseIntOrNull(String s) {
	    if (s == null) return null;
	    try {
	        return Integer.valueOf(s);
	    } catch (NumberFormatException e) {
	        return null;
	    }
	}

	private java.sql.Date parseDateOrNull(String s) {
	    if (s == null) return null;
	    try {
	        return java.sql.Date.valueOf(s); 
	    } catch (IllegalArgumentException e) {
	        return null;
	    }
	}


}