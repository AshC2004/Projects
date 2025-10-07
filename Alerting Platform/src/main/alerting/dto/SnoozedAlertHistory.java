package com.example.alerting.dto;

import com.example.alerting.model.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnoozedAlertHistory {
    private String alertId;
    private String title;
    private String message;
    private Severity severity;
    private LocalDateTime snoozeUntil;
    private boolean isCurrentlySnoozed;
    private boolean isRead;
    private AlertStatus alertStatus;
    private boolean isArchived;
}
