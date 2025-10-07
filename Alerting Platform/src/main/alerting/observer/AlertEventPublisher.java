package com.example.alerting.observer;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AlertEventPublisher {
    private final List<AlertEventListener> listeners = new CopyOnWriteArrayList<>();

    public void register(AlertEventListener listener) {
        listeners.add(listener);
    }

    public void unregister(AlertEventListener listener) {
        listeners.remove(listener);
    }

    public void publish(AlertEvent event) {
        for (AlertEventListener l : listeners) {
            try {
                l.onAlertEvent(event);
            } catch (Exception ignored) {}
        }
    }
}
