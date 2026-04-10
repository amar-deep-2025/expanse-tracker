package com.amar.fullstack.expanse_tracker_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "expanses")
public class Expanse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ExpanseCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String description;

    @Column(name = "expanse_date", nullable = false)
    private LocalDateTime expanseDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "Type")
    @Enumerated(EnumType.STRING)
    private Type type;

    public Expanse() {
    }

    public Expanse(Long id, String name, Double amount, ExpanseCategory category, User user, String description,
            LocalDateTime expanseDate, LocalDateTime createdAt, LocalDateTime updatedAt, Type type) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.user = user;
        this.description = description;
        this.expanseDate = expanseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type = type;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt=this.createdAt;
        this.expanseDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public ExpanseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpanseCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getExpanseDate() {
        return expanseDate;
    }

    public void setExpanseDate(LocalDateTime expanseDate) {
        this.expanseDate = expanseDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
