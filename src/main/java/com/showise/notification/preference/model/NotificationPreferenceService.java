package com.showise.notification.preference.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("notification_preference")
public class NotificationPreferenceService {

	@Autowired
    private NotificationPreferenceRepository repository;

    private static final short STAT_PENDING = 0; // 待寄

    // 排程用：抓指定日期 + 待寄
    public List<NotificationPreferenceVO> findPendingBySendDate(LocalDate sendDate) {
        return repository.findByNotiPrefStimeAndNotiPrefStat(sendDate, STAT_PENDING);
    }

    public void addNotificationPreference(NotificationPreferenceVO vo) {
        repository.save(vo);
    }

    public void updateNotificationPreference(NotificationPreferenceVO vo) {
        repository.save(vo);
    }

    public void deleteNotificationPreference(Integer notiPrefNo) {
        repository.deleteById(notiPrefNo);
    }

    public NotificationPreferenceVO getOneNotificationPreference(Integer notiPrefNo) {
        return repository.findById(notiPrefNo).orElse(null);
    }

    public List<NotificationPreferenceVO> getAll() {
        return repository.findAll();
    }
}
