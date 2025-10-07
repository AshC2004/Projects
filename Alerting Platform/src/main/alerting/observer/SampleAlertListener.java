package com.example.alerting.observer;

import com.example.alerting.model.NotificationDelivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SampleAlertListener implements AlertEventListener {
    private static final Logger logger = LoggerFactory.getLogger(SampleAlertListener.class);

    private final AlertEventPublisher publisher;

    public SampleAlertListener(AlertEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostConstruct
    public void init() {
        publisher.register(this);
    }

    @Override
    public void onAlertEvent(AlertEvent event) {
        NotificationDelivery d = event.getDelivery();
        logger.info("SampleAlertListener observed delivery: {} to user {} via {}", d.getDeliveryId(), d.getUserId(), d.getChannel());
    }
}
