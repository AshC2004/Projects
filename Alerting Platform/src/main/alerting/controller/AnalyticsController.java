package com.example.alerting.controller;

import com.example.alerting.dto.AnalyticsMetrics;
import com.example.alerting.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/alerts")
    public ResponseEntity<AnalyticsMetrics> getAnalytics() {
        return ResponseEntity.ok(analyticsService.computeMetrics());
    }
}
