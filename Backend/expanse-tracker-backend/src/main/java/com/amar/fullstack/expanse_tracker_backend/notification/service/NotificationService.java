package com.amar.fullstack.expanse_tracker_backend.notification.service;
import com.amar.fullstack.expanse_tracker_backend.dtos.NotificationRequest;
import com.amar.fullstack.expanse_tracker_backend.entity.NotificationType;
import com.amar.fullstack.expanse_tracker_backend.notification.factory.NotificationFactory;
import com.amar.fullstack.expanse_tracker_backend.notification.strategy.NotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationFactory factory;

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationService.class);

    public void send(NotificationRequest request){

        if(request == null){
            throw new IllegalArgumentException("Request cannot be null");
        }

        if(request.getTypes() == null || request.getTypes().isEmpty()){
            throw new IllegalArgumentException("At least one notification type required");
        }

        for(NotificationType type : request.getTypes()){

            logger.info("Sending {} notification", type);

            NotificationStrategy strategy = factory.getStrategy(type);
            strategy.send(request);
        }
    }
}