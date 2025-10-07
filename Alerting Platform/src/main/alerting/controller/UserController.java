package com.example.alerting.controller;

import com.example.alerting.dto.*;
import com.example.alerting.exception.BadRequestException;
import com.example.alerting.exception.UserNotFoundException;
import com.example.alerting.model.*;
import com.example.alerting.repository.*;
import com.example.alerting.service.PreferenceService;
import com.example.alerting.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/alerts")
public class UserController {
    private final UserRepository userRepository;
    private final AlertRepository alertRepository;
    private final UserAlertPreferenceRepository preferenceRepository;
    private final PreferenceService preferenceService;
    private final AlertService alertService;

    public UserController(UserRepository userRepository, AlertRepository alertRepository,
                          UserAlertPreferenceRepository preferenceRepository,
                          PreferenceService preferenceService, AlertService alertService) {
        this.userRepository = userRepository;
        this.alertRepository = alertRepository;
        this.preferenceRepository = preferenceRepository;
        this.preferenceService = preferenceService;
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String,Object>>> fetchActiveAlerts(@PathVariable String userId) {
        User user = userRepository.get(userId);
        if (user == null) throw new UserNotFoundException("User not found");
        LocalDateTime now = LocalDateTime.now();
        List<Alert> activeAlerts = alertRepository.listAll().stream()
                .filter(alert -> !alert.isArchived())
                .filter(alert -> alertService.computeStatus(alert, now).equals(AlertStatus.ACTIVE))
                .filter(alert -> isUserRelevant(alert, user))
                .toList();

        List<Map<String,Object>> response = activeAlerts.stream()
                .map(alert -> mapAlertToResponse(alert, userId, now))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{alertId}/read")
    public ResponseEntity<Map<String,String>> markAlertRead(@PathVariable String userId, @PathVariable String alertId) {
        User user = userRepository.get(userId);
        Alert alert = alertRepository.get(alertId);
        if (user == null || alert == null) {
            throw new UserNotFoundException("User or Alert not found");
        }
        preferenceService.markRead(alertId, userId);
        return ResponseEntity.ok(Map.of("alert_id", alertId, "user_id", userId, "status", "read"));
    }

    @PatchMapping("/{alertId}/unread")
    public ResponseEntity<Map<String,String>> markAlertUnread(@PathVariable String userId, @PathVariable String alertId) {
        User user = userRepository.get(userId);
        Alert alert = alertRepository.get(alertId);
        if (user == null || alert == null) {
            throw new UserNotFoundException("User or Alert not found");
        }
        // mark unread
        var pref = preferenceRepository.get(alertId, userId);
        if (pref == null) {
            pref = new com.example.alerting.model.UserAlertPreference(alertId, userId, false, null, null);
            preferenceRepository.create(pref);
        }
        pref.markUnread();
        preferenceRepository.update(pref);
        return ResponseEntity.ok(Map.of("alert_id", alertId, "user_id", userId, "status", "unread"));
    }

    @PostMapping("/{alertId}/snooze")
    public ResponseEntity<Map<String,Object>> snoozeAlert(@PathVariable String userId, @PathVariable String alertId) {
        User user = userRepository.get(userId);
        Alert alert = alertRepository.get(alertId);
        if (user == null || alert == null) {
            throw new UserNotFoundException("User or Alert not found");
        }
        if (!alertService.computeStatus(alert, LocalDateTime.now()).equals(AlertStatus.ACTIVE)) {
            throw new BadRequestException("Alert is not active");
        }
        if (!isUserRelevant(alert, user)) {
            throw new BadRequestException("User not in alert audience");
        }
        preferenceService.snooze(alertId, userId);
        UserAlertPreference pref = preferenceRepository.get(alertId, userId);
        return ResponseEntity.ok(Map.of(
                "alert_id", alertId,
                "user_id", userId,
                "snooze_until", pref.getSnoozeUntil() == null ? null : pref.getSnoozeUntil().toString()
        ));
    }

    private boolean isUserRelevant(Alert alert, User user) {
        switch (alert.getVisibilityType()) {
            case USER:
                return alert.getTargetUserIds().contains(user.getUserId());
            case TEAM:
                return alert.getTargetTeamIds().stream().anyMatch(user.getTeams()::contains);
            default:
                return true;
        }
    }

    private Map<String,Object> mapAlertToResponse(Alert alert, String userId, LocalDateTime now) {
        UserAlertPreference pref = preferenceRepository.listByUser(userId).stream()
                .filter(p -> p.getAlertId().equals(alert.getAlertId()))
                .findFirst()
                .orElse(null);

        boolean isRead = pref != null && pref.isRead();
        boolean isSnoozed = pref != null && pref.isCurrentlySnoozed(now);
        String snoozeUntil = pref != null && pref.getSnoozeUntil() != null ? pref.getSnoozeUntil().toString() : null;

        return Map.of(
                "alert_id", alert.getAlertId(),
                "title", alert.getTitle(),
                "message", alert.getMessage(),
                "severity", alert.getSeverity().name().toLowerCase(),
                "start_time", alert.getStartTime().toString(),
                "expiry_time", alert.getExpiryTime().toString(),
                "is_read", isRead,
                "is_snoozed", isSnoozed,
                "snooze_until", snoozeUntil
        );
    }
}
