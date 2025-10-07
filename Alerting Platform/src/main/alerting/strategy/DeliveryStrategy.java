package com.example.alerting.strategy;

import com.example.alerting.model.Alert;
import com.example.alerting.model.User;
import com.example.alerting.model.NotificationDelivery;

public interface DeliveryStrategy {
    NotificationDelivery deliver(Alert alert, User user);
}
