package com.example.alerting.repository;

import com.example.alerting.model.UserAlertPreference;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class UserAlertPreferenceRepository {
    private final Map<String, UserAlertPreference> preferences = new ConcurrentHashMap<>();

    private String key(String alertId, String userId) { return alertId + "::" + userId; }

    public UserAlertPreference get(String alertId, String userId) {
        return preferences.get(key(alertId, userId));
    }

    public void create(UserAlertPreference pref) {
        preferences.putIfAbsent(key(pref.getAlertId(), pref.getUserId()), pref);
    }

    public void update(UserAlertPreference pref) {
        preferences.put(key(pref.getAlertId(), pref.getUserId()), pref);
    }

    public List<UserAlertPreference> listAll() { return new ArrayList<>(preferences.values()); }

    public List<UserAlertPreference> listByUser(String userId) {
        return preferences.values().stream()
                .filter(pref -> pref.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}
