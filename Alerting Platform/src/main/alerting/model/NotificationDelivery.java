package com.example.alerting.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDelivery {
    private String deliveryId;
    private String alertId;
    private String userId;
    private DeliveryChannel channel;
    private LocalDateTime deliveredAt;
    private boolean isDelivered = false;
    private String errorMessage;
}
