package com.example.alerting.service;

import com.example.alerting.model.UserAlertPreference;
import com.example.alerting.repository.UserAlertPreferenceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class PreferenceService {
    private final UserAlertPreferenceRepository preferenceRepository;

    public PreferenceService(UserAlertPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public void markRead(String alertId, String userId) {
        UserAlertPreference pref = preferenceRepository.get(alertId, userId);
        if (pref == null) {
            pref = new UserAlertPreference(alertId, userId, true, null, null);
            pref.setState(new com.example.alerting.state.ReadState());
            preferenceRepository.create(pref);
            return;
        }
        pref.setState(new com.example.alerting.state.ReadState());
        pref.markRead();
        preferenceRepository.update(pref);
    }

    // Snooze for rest of user's day (Asia/Kolkata timezone by default)
    public void snooze(String alertId, String userId) {
        UserAlertPreference pref = preferenceRepository.get(alertId, userId);
        if (pref == null) {
            pref = new UserAlertPreference(alertId, userId, false, null, null);
            preferenceRepository.create(pref);
        }
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        pref.applySnooze(endOfDay);
        preferenceRepository.update(pref);
    }
}
