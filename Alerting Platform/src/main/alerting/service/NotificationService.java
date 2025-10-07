package com.example.alerting.service;

import com.example.alerting.model.*;
import com.example.alerting.repository.*;
import com.example.alerting.strategy.DeliveryStrategy;
import com.example.alerting.strategy.DeliveryStrategyFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final UserAlertPreferenceRepository preferenceRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final DeliveryStrategyFactory strategyFactory;
    private final AlertService alertService;
    private final com.example.alerting.observer.AlertEventPublisher eventPublisher;

    public NotificationService(AlertRepository alertRepository, UserRepository userRepository,
                               UserAlertPreferenceRepository preferenceRepository,
                               NotificationDeliveryRepository deliveryRepository,
                               DeliveryStrategyFactory strategyFactory,
                               AlertService alertService,
                               com.example.alerting.observer.AlertEventPublisher eventPublisher) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
        this.deliveryRepository = deliveryRepository;
        this.strategyFactory = strategyFactory;
        this.alertService = alertService;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedRateString = "${notification.fixedRateMillis:7200000}")
    public void processReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Alert> activeAlerts = getActiveRemindableAlerts(now);
        for (Alert alert : activeAlerts) {
            List<User> targetUsers = getTargetUsers(alert);
            for (User user : targetUsers) {
                if (shouldRemind(alert, user, now)) {
                    deliverAlertToUser(alert, user);
                    updateNextReminderTime(user, alert, now);
                }
            }
        }
    }

    private List<Alert> getActiveRemindableAlerts(LocalDateTime now) {
        return alertRepository.listAll().stream()
                .filter(alert -> !alert.isArchived())
                .filter(alert -> alertService.computeStatus(alert, now).equals(AlertStatus.ACTIVE))
                .filter(Alert::isReminderEnabled)
                .toList();
    }

    private List<User> getTargetUsers(Alert alert) {
        switch (alert.getVisibilityType()) {
            case USER:
                return userRepository.getUsersByIds(alert.getTargetUserIds());
            case TEAM:
                return userRepository.getUsersByTeamIds(alert.getTargetTeamIds());
            default:
                return userRepository.listAll();
        }
    }

    private boolean shouldRemind(Alert alert, User user, LocalDateTime now) {
        UserAlertPreference pref = preferenceRepository.get(alert.getAlertId(), user.getUserId());
        if (pref == null) {
            pref = new UserAlertPreference(alert.getAlertId(), user.getUserId(), false, null, null);
            preferenceRepository.create(pref);
            return true;
        }
        if (pref.isRead()) return false;
        if (pref.getSnoozeUntil() != null && now.isBefore(pref.getSnoozeUntil())) return false;
        return pref.getNextReminderTime() == null || now.isAfter(pref.getNextReminderTime());
    }

    private void deliverAlertToUser(Alert alert, User user) {
        for (DeliveryChannel channel : alert.getDeliveryChannels()) {
            DeliveryStrategy strategy = strategyFactory.getStrategy(channel);
            if (strategy == null) continue;
            NotificationDelivery delivery = strategy.deliver(alert, user);
            deliveryRepository.create(delivery);
            try {
                eventPublisher.publish(new com.example.alerting.observer.AlertEvent(delivery));
            } catch (Exception ignored) {}
        }
    }

    private void updateNextReminderTime(User user, Alert alert, LocalDateTime now) {
        UserAlertPreference pref = preferenceRepository.get(alert.getAlertId(), user.getUserId());
        if (pref != null) {
            pref.setNextReminderTime(now.plusSeconds(alert.getReminderFrequencySeconds()));
            preferenceRepository.update(pref);
        }
    }
}
