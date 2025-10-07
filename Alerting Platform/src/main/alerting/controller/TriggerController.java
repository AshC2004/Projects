package com.example.alerting.controller;

import com.example.alerting.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trigger-reminders")
public class TriggerController {
    private final NotificationService notificationService;

    public TriggerController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Map<String,String>> trigger() {
        notificationService.processReminders();
        return ResponseEntity.ok(Map.of("status", "success", "message", "Reminders processed manually"));
    }
}
