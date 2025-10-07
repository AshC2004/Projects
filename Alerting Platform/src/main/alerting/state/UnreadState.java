package com.example.alerting.state;

import com.example.alerting.model.UserAlertPreference;

import java.time.LocalDateTime;

public class UnreadState implements PreferenceState {
    @Override
    public void markRead(UserAlertPreference pref) {
        pref.setRead(true);
        pref.setSnoozeUntil(null);
        pref.setNextReminderTime(null);
        pref.setState(new ReadState());
    }

    @Override
    public void markUnread(UserAlertPreference pref) {
        // already unread
    }

    @Override
    public void snooze(UserAlertPreference pref, LocalDateTime snoozeUntil) {
        pref.setSnoozeUntil(snoozeUntil);
        pref.setState(new SnoozedState());
    }

    @Override
    public String name() { return "UNREAD"; }
}
