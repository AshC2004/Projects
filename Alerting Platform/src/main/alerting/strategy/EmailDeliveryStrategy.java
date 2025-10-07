package com.example.alerting.strategy;

import com.example.alerting.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EmailDeliveryStrategy implements DeliveryStrategy {
    @Override
    public NotificationDelivery deliver(Alert alert, User user) {
        String deliveryId = "email_" + alert.getAlertId() + "_" + user.getUserId() + "_" + UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        // Mock email send - mark delivered true for MVP
        return new NotificationDelivery(deliveryId, alert.getAlertId(), user.getUserId(), DeliveryChannel.EMAIL, now, true, null);
    }
}
