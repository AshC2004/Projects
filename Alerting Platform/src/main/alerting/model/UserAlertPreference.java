package com.example.alerting.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAlertPreference {
    private String alertId;
    private String userId;
    private boolean isRead = false;
    private LocalDateTime snoozeUntil;
    private LocalDateTime nextReminderTime;

    public boolean isCurrentlySnoozed(LocalDateTime now) {
        return snoozeUntil != null && now.isBefore(snoozeUntil);
    }

    // State transition helpers
    public void markRead() {
        if (this.state == null) this.state = new com.example.alerting.state.ReadState();
        this.state.markRead(this);
    }

    public void markUnread() {
        if (this.state == null) this.state = new com.example.alerting.state.UnreadState();
        this.state.markUnread(this);
    }

    public void applySnooze(java.time.LocalDateTime snoozeUntil) {
        if (this.state == null) this.state = new com.example.alerting.state.SnoozedState();
        this.state.snooze(this, snoozeUntil);
    }
}

