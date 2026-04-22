package com.amar.fullstack.expanse_tracker_backend.notification.strategy;

import com.amar.fullstack.expanse_tracker_backend.dtos.NotificationRequest;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.repository.UserRepository;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.twilio.Twilio;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationStrategy implements NotificationStrategy{

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromNumber;


    @PostConstruct
    public void init(){
        Twilio.init(accountSid,authToken);
    }

    @Override
    public void send(NotificationRequest request) {
        String sms=request.getSmsMessage();

        if (sms==null || sms.isEmpty()){
            sms=request.getMessage();
        }
        Message.creator(
                new PhoneNumber(request.getPhone()),
                new PhoneNumber(fromNumber),
                sms
        ).create();

        System.out.println("SMS sent from "+fromNumber);
        System.out.println("SMS sent to "+request.getPhone());

    }
}
