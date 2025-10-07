package com.example.alerting.dto;

import com.example.alerting.model.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {
    private String alertId;
    private String title;
    private String message;
    private Severity severity;
    private LocalDateTime startTime;
    private LocalDateTime expiryTime;
    private int reminderFrequencySeconds;
    private VisibilityType visibilityType;
    private List<String> targetTeamIds;
    private List<String> targetUserIds;
    private List<DeliveryChannel> deliveryChannels;
    private boolean isReminderEnabled;
    private boolean isArchived;
    private AlertStatus status;
}
