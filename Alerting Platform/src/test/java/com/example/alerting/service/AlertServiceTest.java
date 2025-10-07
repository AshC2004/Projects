package com.example.alerting.service;

import com.example.alerting.model.Alert;
import com.example.alerting.model.AlertStatus;
import com.example.alerting.model.Severity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlertServiceTest {

    @Test
    public void testComputeStatus() {
        AlertService svc = new AlertService(null);
        Alert a = new Alert();
        a.setAlertId("a1");
        a.setTitle("t");
        a.setMessage("m");
        a.setSeverity(Severity.INFO);
        a.setStartTime(LocalDateTime.now().minusHours(1));
        a.setExpiryTime(LocalDateTime.now().plusHours(1));
        a.setArchived(false);

        AlertStatus status = svc.computeStatus(a, LocalDateTime.now());
        assertEquals(AlertStatus.ACTIVE, status);
    }
}
