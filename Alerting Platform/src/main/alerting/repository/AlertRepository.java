package com.example.alerting.repository;

import com.example.alerting.model.Alert;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AlertRepository {
    private final Map<String, Alert> alerts = new ConcurrentHashMap<>();

    public void create(Alert alert) {
        if (alert.getAlertId() == null) {
            alert.setAlertId(UUID.randomUUID().toString());
        }
        alerts.put(alert.getAlertId(), alert);
    }

    public Alert get(String alertId) { return alerts.get(alertId); }

    public void update(Alert alert) {
        if (alert.getAlertId() != null && alerts.containsKey(alert.getAlertId())) {
            alerts.put(alert.getAlertId(), alert);
        }
    }

    public void delete(String alertId) { alerts.remove(alertId); }

    public List<Alert> listAll() { return new ArrayList<>(alerts.values()); }

    public List<Alert> listActive() {
        return alerts.values().stream().collect(Collectors.toList());
    }
}
