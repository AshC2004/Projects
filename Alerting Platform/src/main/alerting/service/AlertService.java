package com.example.alerting.service;

import com.example.alerting.model.*;
import com.example.alerting.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public AlertStatus computeStatus(Alert alert, LocalDateTime now) {
        if (alert.isArchived()) return AlertStatus.ARCHIVED;
        if (now.isBefore(alert.getStartTime())) return AlertStatus.SCHEDULED;
        if (now.isAfter(alert.getExpiryTime())) return AlertStatus.EXPIRED;
        return AlertStatus.ACTIVE;
    }

    public List<Alert> listAlerts(LocalDateTime now, Severity severity, AlertStatus status, VisibilityType visibilityType) {
        return alertRepository.listAll().stream()
                .filter(alert -> {
                    AlertStatus currentStatus = computeStatus(alert, now);
                    if (severity != null && !alert.getSeverity().equals(severity)) return false;
                    if (status != null && !currentStatus.equals(status)) return false;
                    if (visibilityType != null && !alert.getVisibilityType().equals(visibilityType)) return false;
                    return true;
                })
                .toList();
    }
}
