package com.amar.fullstack.expanse_tracker_backend.notification.factory;


import com.amar.fullstack.expanse_tracker_backend.entity.NotificationType;
import com.amar.fullstack.expanse_tracker_backend.notification.strategy.EmailNotificationStrategy;
import com.amar.fullstack.expanse_tracker_backend.notification.strategy.NotificationStrategy;
import com.amar.fullstack.expanse_tracker_backend.notification.strategy.SmsNotificationStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationFactory {

    private final Map<NotificationType, NotificationStrategy> map;

    public NotificationFactory(EmailNotificationStrategy email, SmsNotificationStrategy sms) {

        map = new HashMap<>();
        map.put(NotificationType.EMAIL, email);
        map.put(NotificationType.SMS, sms);
    }

    public NotificationStrategy getStrategy(NotificationType type) {
        NotificationStrategy strategy = map.get(type);

        if (strategy == null) {
            throw new RuntimeException("No strategy found for " + type);
        }
        return strategy;
    }
}
