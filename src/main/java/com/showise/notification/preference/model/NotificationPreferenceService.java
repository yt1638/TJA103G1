package com.showise.notification.preference.model;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("notification_preference")
public class NotificationPreferenceService {

    @Autowired
    private NotificationPreferenceRepository repository;

    // 新增
    public void addNotificationPreference(NotificationPreferenceVO notificationPreferenceVO) {
        repository.save(notificationPreferenceVO);
    }

    // 修改
    public void updateNotificationPreference(NotificationPreferenceVO notificationPreferenceVO) {
        repository.save(notificationPreferenceVO);
    }

    // 刪除（✅補上，配合 Controller）
    public void deleteNotificationPreference(Integer notiPrefNo) {
        repository.deleteById(notiPrefNo);
    }

    // 單筆查詢
    public NotificationPreferenceVO getOneNotificationPreference(Integer notiPrefNo) {
        Optional<NotificationPreferenceVO> optional = repository.findById(notiPrefNo);
        return optional.orElse(null);
    }

    // 全部查詢
    public List<NotificationPreferenceVO> getAll() {
        return repository.findAll();
    }

    // 複合查詢（✅你現在的 Repository JPQL 就是吃這三個）
    public List<NotificationPreferenceVO> compositeQuery(Integer memberId, Integer movieId,Date sendDate) {
        return repository.compositeQuery(memberId, movieId, sendDate);
    }
    
    public void save(NotificationPreferenceVO vo) {
        repository.save(vo);
    }
}
