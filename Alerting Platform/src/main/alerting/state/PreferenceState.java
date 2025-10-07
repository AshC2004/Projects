package com.example.alerting.state;

import com.example.alerting.model.UserAlertPreference;

import java.time.LocalDateTime;

public interface PreferenceState {
    void markRead(UserAlertPreference pref);
    void markUnread(UserAlertPreference pref);
    void snooze(UserAlertPreference pref, LocalDateTime snoozeUntil);
    String name();
}
