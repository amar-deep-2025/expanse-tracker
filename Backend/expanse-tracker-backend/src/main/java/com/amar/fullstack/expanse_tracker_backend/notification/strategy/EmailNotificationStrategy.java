package com.amar.fullstack.expanse_tracker_backend.notification.strategy;

import com.amar.fullstack.expanse_tracker_backend.dtos.NotificationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailNotificationStrategy implements NotificationStrategy {

    @Value("${resend.api.key}")
    private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void send(NotificationRequest request) {

        String subject = request.getSubject() != null
                ? request.getSubject()
                : "Notification from Expanse Tracker";

        String body = request.getMessage();

        if (body == null || body.isEmpty()) {
            body = "No content available";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        Map<String, Object> email = new HashMap<>();

        email.put("from", "Expense Tracker Team");
        email.put("to", request.getEmail());
        email.put("subject", subject);
        email.put("text", body);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(email, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.resend.com/emails",
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(
                    "Failed to send email through Resend: " + response.getBody()
            );
        }

        System.out.println("Email sent to " + request.getEmail());
    }
}