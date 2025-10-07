package com.example.alerting.state;

import com.example.alerting.model.UserAlertPreference;

import java.time.LocalDateTime;

public class SnoozedState implements PreferenceState {
    @Override
    public void markRead(UserAlertPreference pref) {
        pref.setRead(true);
        pref.setSnoozeUntil(null);
        pref.setState(new ReadState());
    }

    @Override
    public void markUnread(UserAlertPreference pref) {
        pref.setRead(false);
        pref.setSnoozeUntil(null);
        pref.setState(new UnreadState());
    }

    @Override
    public void snooze(UserAlertPreference pref, LocalDateTime snoozeUntil) {
        pref.setSnoozeUntil(snoozeUntil);
        // remain in snoozed state
    }

    @Override
    public String name() { return "SNOOZED"; }
}
