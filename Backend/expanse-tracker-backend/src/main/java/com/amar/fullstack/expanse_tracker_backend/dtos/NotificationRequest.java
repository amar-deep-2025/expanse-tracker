package com.amar.fullstack.expanse_tracker_backend.dtos;

import com.amar.fullstack.expanse_tracker_backend.entity.NotificationType;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class NotificationRequest {

    private  String email;

    @Pattern(regexp = "^\\+[1-9]\\d{7,14}$")
    private String phone;

    private String message;

    private String smsMessage;

    private String subject;

    private byte[] attachment;

    private List<NotificationType> types;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSmsMessage() {
        return smsMessage;
    }

    public void setSmsMessage(String smsMessage) {
        this.smsMessage = smsMessage;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    public List<NotificationType> getTypes() {
        return types;
    }

    public void setTypes(List<NotificationType> types) {
        this.types = types;
    }
}
