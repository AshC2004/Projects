package com.example.alerting.repository;

import com.example.alerting.model.NotificationDelivery;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationDeliveryRepository {
    private final Map<String, NotificationDelivery> deliveries = new ConcurrentHashMap<>();

    public void create(NotificationDelivery d) {
        deliveries.put(d.getDeliveryId(), d);
    }

    public List<NotificationDelivery> listAll() {
        return new ArrayList<>(deliveries.values());
    }
}
