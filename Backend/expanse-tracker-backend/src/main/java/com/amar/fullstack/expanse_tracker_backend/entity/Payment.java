package com.amar.fullstack.expanse_tracker_backend.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderId;


    private String paymentId;


    private String signature;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(Long id, String orderId, String paymentId, String signature, int amount, PaymentStatus status, User user, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.signature = signature;
        this.amount = amount;
        this.status = status;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
