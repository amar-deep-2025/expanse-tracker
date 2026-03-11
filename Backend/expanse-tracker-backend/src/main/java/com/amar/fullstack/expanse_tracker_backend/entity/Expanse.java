package com.amar.fullstack.expanse_tracker_backend.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="expanses")
public class Expanse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExpanseCategory category;

    private String description;

    @Column(name="expanse_date", nullable = false)
    private LocalDateTime expanseDate;

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Expanse() {
    }

    public Expanse(Long id, String name, Double amount, ExpanseCategory category, String description, LocalDateTime expanseDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.expanseDate = expanseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.expanseDate=LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt=LocalDateTime.now();
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
}
