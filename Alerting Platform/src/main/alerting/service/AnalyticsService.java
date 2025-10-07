package com.example.alerting.service;

import com.example.alerting.dto.AnalyticsMetrics;
import com.example.alerting.model.UserAlertPreference;
import com.example.alerting.repository.AlertRepository;
import com.example.alerting.repository.NotificationDeliveryRepository;
import com.example.alerting.repository.UserAlertPreferenceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final AlertRepository alertRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final UserAlertPreferenceRepository preferenceRepository;

    public AnalyticsService(AlertRepository alertRepository, NotificationDeliveryRepository deliveryRepository,
                            UserAlertPreferenceRepository preferenceRepository) {
        this.alertRepository = alertRepository;
        this.deliveryRepository = deliveryRepository;
        this.preferenceRepository = preferenceRepository;
    }

    public AnalyticsMetrics computeMetrics() {
        int totalAlerts = alertRepository.listAll().size();
        int delivered = deliveryRepository.listAll().size();
        int read = (int) preferenceRepository.listAll().stream().filter(UserAlertPreference::isRead).count();

        Map<String, Integer> snoozedCounts = preferenceRepository.listAll().stream()
                .filter(pref -> pref.getSnoozeUntil() != null && pref.getSnoozeUntil().isAfter(LocalDateTime.now()))
                .collect(Collectors.groupingBy(UserAlertPreference::getAlertId, Collectors.reducing(0, e -> 1, Integer::sum)));

        Map<String, Integer> severityBreakdown = alertRepository.listAll().stream()
                .collect(Collectors.groupingBy(a -> a.getSeverity().name(), Collectors.reducing(0, e -> 1, Integer::sum)));

        return new AnalyticsMetrics(totalAlerts, delivered, read, snoozedCounts, severityBreakdown);
    }
}
