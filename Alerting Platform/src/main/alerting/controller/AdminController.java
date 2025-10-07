package com.example.alerting.controller;

import com.example.alerting.dto.AlertResponse;
import com.example.alerting.dto.AlertUpdate;
import com.example.alerting.exception.BadRequestException;
import com.example.alerting.exception.AlertNotFoundException;
import com.example.alerting.model.*;
import com.example.alerting.repository.AlertRepository;
import com.example.alerting.repository.TeamRepository;
import com.example.alerting.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/alerts")
public class AdminController {
    private final AlertRepository alertRepository;
    private final AlertService alertService;
    private final TeamRepository teamRepository;

    public AdminController(AlertRepository alertRepository, AlertService alertService, TeamRepository teamRepository) {
        this.alertRepository = alertRepository;
        this.alertService = alertService;
        this.teamRepository = teamRepository;
    }

    @PostMapping
    public ResponseEntity<Alert> createAlert(@Valid @RequestBody Alert alert) {
        validateVisibility(alert);
        if (alert.getAlertId() == null) alert.setAlertId(UUID.randomUUID().toString());
        alertRepository.create(alert);
        return ResponseEntity.status(201).body(alert);
    }

    @PutMapping("/{alertId}")
    public ResponseEntity<Alert> updateAlert(@PathVariable String alertId, @Valid @RequestBody AlertUpdate update) {
        Alert existingAlert = alertRepository.get(alertId);
        if (existingAlert == null) {
            throw new AlertNotFoundException("Alert not found");
        }
        updateFields(existingAlert, update);
        validateVisibility(existingAlert);
        alertRepository.update(existingAlert);
        return ResponseEntity.ok(existingAlert);
    }

    @PatchMapping("/{alertId}/archive")
    public ResponseEntity<Map<String,String>> archiveAlert(@PathVariable String alertId) {
        Alert alert = alertRepository.get(alertId);
        if (alert == null) throw new AlertNotFoundException("Alert not found");
        alert.setArchived(true);
        alertRepository.update(alert);
        return ResponseEntity.ok(Map.of("alert_id", alertId, "status", "archived"));
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> listAlerts(
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) VisibilityType visibilityType
    ) {
        LocalDateTime now = LocalDateTime.now();
        List<Alert> alerts = alertService.listAlerts(now, severity, status, visibilityType);
        List<AlertResponse> responses = alerts.stream().map(a -> mapToResponse(a, now)).toList();
        return ResponseEntity.ok(responses);
    }

    private void validateVisibility(Alert alert) {
        if (alert.getVisibilityType() == VisibilityType.TEAM && alert.getTargetTeamIds().isEmpty()) {
            throw new BadRequestException("Team visibility requires target_team_ids");
        }
        if (alert.getVisibilityType() == VisibilityType.USER && alert.getTargetUserIds().isEmpty()) {
            throw new BadRequestException("User visibility requires target_user_ids");
        }
        if (alert.getVisibilityType() == VisibilityType.ORGANIZATION &&
                (!alert.getTargetTeamIds().isEmpty() || !alert.getTargetUserIds().isEmpty())) {
            throw new BadRequestException("Organization visibility shouldn't have team/user targets");
        }
    }

    private void updateFields(Alert existingAlert, AlertUpdate update) {
        if (update.getTitle() != null) existingAlert.setTitle(update.getTitle());
        if (update.getMessage() != null) existingAlert.setMessage(update.getMessage());
        if (update.getSeverity() != null) existingAlert.setSeverity(update.getSeverity());
        if (update.getStartTime() != null) existingAlert.setStartTime(update.getStartTime());
        if (update.getExpiryTime() != null) existingAlert.setExpiryTime(update.getExpiryTime());
        if (update.getReminderFrequencySeconds() != null) existingAlert.setReminderFrequencySeconds(update.getReminderFrequencySeconds());
        if (update.getVisibilityType() != null) existingAlert.setVisibilityType(update.getVisibilityType());
        if (update.getTargetTeamIds() != null) existingAlert.setTargetTeamIds(update.getTargetTeamIds());
        if (update.getTargetUserIds() != null) existingAlert.setTargetUserIds(update.getTargetUserIds());
        if (update.getDeliveryChannels() != null) existingAlert.setDeliveryChannels(update.getDeliveryChannels());
        if (update.getIsReminderEnabled() != null) existingAlert.setReminderEnabled(update.getIsReminderEnabled());
        if (update.getIsArchived() != null) existingAlert.setArchived(update.getIsArchived());
    }

    private AlertResponse mapToResponse(Alert alert, LocalDateTime now) {
        AlertStatus currentStatus = alert.isArchived() ? AlertStatus.ARCHIVED : alertService.computeStatus(alert, now);
        return new AlertResponse(
                alert.getAlertId(),
                alert.getTitle(),
                alert.getMessage(),
                alert.getSeverity(),
                alert.getStartTime(),
                alert.getExpiryTime(),
                alert.getReminderFrequencySeconds(),
                alert.getVisibilityType(),
                alert.getTargetTeamIds(),
                alert.getTargetUserIds(),
                alert.getDeliveryChannels(),
                alert.isReminderEnabled(),
                alert.isArchived(),
                currentStatus
        );
    }
}
