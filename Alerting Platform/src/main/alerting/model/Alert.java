package com.example.alerting.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    private String alertId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Severity is required")
    private Severity severity;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "Expiry time is required")
    @Future(message = "Expiry time must be in the future")
    private LocalDateTime expiryTime;

    private int reminderFrequencySeconds = 7200;
    private VisibilityType visibilityType = VisibilityType.ORGANIZATION;
    private List<String> targetTeamIds = List.of();
    private List<String> targetUserIds = List.of();
    private List<DeliveryChannel> deliveryChannels = List.of(DeliveryChannel.IN_APP);
    private boolean isReminderEnabled = true;
    private boolean isArchived = false;
}
