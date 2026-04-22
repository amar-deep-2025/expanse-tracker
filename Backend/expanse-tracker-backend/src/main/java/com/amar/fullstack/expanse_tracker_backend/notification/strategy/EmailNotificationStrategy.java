package com.amar.fullstack.expanse_tracker_backend.notification.strategy;

import com.amar.fullstack.expanse_tracker_backend.dtos.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationStrategy implements NotificationStrategy{

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(NotificationRequest request){
        SimpleMailMessage mail=new SimpleMailMessage();

        mail.setTo(request.getEmail());
        mail.setSubject(request.getSubject()!=null?request.getSubject():"Notification from Expanse Tracker");

        String body=request.getMessage();
        if (body==null || body.isEmpty()){
            body="No content available";
        }
        mail.setText(body);
        mailSender.send(mail);
        System.out.println("Email sent to "+request.getEmail());
    }
}
