package com.example.alerting.state;

import com.example.alerting.model.UserAlertPreference;

import java.time.LocalDateTime;

public class ReadState implements PreferenceState {
    @Override
    public void markRead(UserAlertPreference pref) {
        // already read
    }

    @Override
    public void markUnread(UserAlertPreference pref) {
        pref.setRead(false);
        pref.setState(new UnreadState());
    }

    @Override
    public void snooze(UserAlertPreference pref, LocalDateTime snoozeUntil) {
        pref.setSnoozeUntil(snoozeUntil);
        pref.setState(new SnoozedState());
    }

    @Override
    public String name() { return "READ"; }
}
