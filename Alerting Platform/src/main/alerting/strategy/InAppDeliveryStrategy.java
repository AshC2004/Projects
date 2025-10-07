package com.example.alerting.strategy;

import com.example.alerting.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class InAppDeliveryStrategy implements DeliveryStrategy {
    @Override
    public NotificationDelivery deliver(Alert alert, User user) {
        String deliveryId = "in_app_" + alert.getAlertId() + "_" + user.getUserId() + "_" + UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        return new NotificationDelivery(deliveryId, alert.getAlertId(), user.getUserId(), DeliveryChannel.IN_APP, now, true, null);
    }
}
