package com.example.alerting;

import com.example.alerting.model.*;
import com.example.alerting.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableScheduling
public class AlertingNotificationPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlertingNotificationPlatformApplication.class, args);
    }

    // Seed data (simple)
    @Bean
    CommandLineRunner seedData(UserRepository userRepository, TeamRepository teamRepository, AlertRepository alertRepository) {
        return args -> {
            Team team1 = new Team("team1", "Engineering");
            Team team2 = new Team("team2", "Marketing");
            teamRepository.create(team1);
            teamRepository.create(team2);

            User user1 = new User("user1", "Alice", List.of("team1"));
            User user2 = new User("user2", "Bob", List.of("team1"));
            User user3 = new User("user3", "Charlie", List.of("team2"));
            userRepository.create(user1);
            userRepository.create(user2);
            userRepository.create(user3);

            Alert alert1 = new Alert();
            alert1.setAlertId(UUID.randomUUID().toString());
            alert1.setTitle("System Maintenance Scheduled");
            alert1.setMessage("Server maintenance scheduled. Downtime expected.");
            alert1.setSeverity(Severity.INFO);
            alert1.setStartTime(LocalDateTime.now());
            alert1.setExpiryTime(LocalDateTime.now().plusDays(3));
            alert1.setVisibilityType(VisibilityType.ORGANIZATION);
            alert1.setDeliveryChannels(List.of(DeliveryChannel.IN_APP));
            alertRepository.create(alert1);

            Alert alert2 = new Alert();
            alert2.setAlertId(UUID.randomUUID().toString());
            alert2.setTitle("Database Latency");
            alert2.setMessage("Engineering team: DB latency increased.");
            alert2.setSeverity(Severity.WARNING);
            alert2.setStartTime(LocalDateTime.now());
            alert2.setExpiryTime(LocalDateTime.now().plusHours(2));
            alert2.setVisibilityType(VisibilityType.TEAM);
            alert2.setTargetTeamIds(List.of("team1"));
            alert2.setReminderFrequencySeconds(0); // no reminders
            alertRepository.create(alert2);
        };
    }
}
