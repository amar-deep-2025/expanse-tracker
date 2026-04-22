package com.amar.fullstack.expanse_tracker_backend.notification.strategy;

import com.amar.fullstack.expanse_tracker_backend.dtos.NotificationRequest;

public interface NotificationStrategy {

    void send(NotificationRequest request);
}
