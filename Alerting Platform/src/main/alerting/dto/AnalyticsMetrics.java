package com.example.alerting.dto;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsMetrics {
    private int totalAlertsCreated;
    private int alertsDelivered;
    private int alertsRead;
    private Map<String, Integer> snoozedCountsPerAlert;
    private Map<String, Integer> severityBreakdown;
}
