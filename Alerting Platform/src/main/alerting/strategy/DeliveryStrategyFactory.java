package com.example.alerting.strategy;

import com.example.alerting.model.DeliveryChannel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeliveryStrategyFactory {
    private final Map<DeliveryChannel, DeliveryStrategy> strategies = new ConcurrentHashMap<>();

    public DeliveryStrategyFactory(InAppDeliveryStrategy inApp, EmailDeliveryStrategy email, SmsDeliveryStrategy sms) {
        strategies.put(DeliveryChannel.IN_APP, inApp);
        strategies.put(DeliveryChannel.EMAIL, email);
        strategies.put(DeliveryChannel.SMS, sms);
    }

    public DeliveryStrategy getStrategy(DeliveryChannel channel) {
        return strategies.get(channel);
    }
}
