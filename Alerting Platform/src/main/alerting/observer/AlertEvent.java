package com.example.alerting.observer;

import com.example.alerting.model.NotificationDelivery;

public class AlertEvent {
    private final NotificationDelivery delivery;

    public AlertEvent(NotificationDelivery delivery) {
        this.delivery = delivery;
    }

    public NotificationDelivery getDelivery() {
        return delivery;
    }
}
