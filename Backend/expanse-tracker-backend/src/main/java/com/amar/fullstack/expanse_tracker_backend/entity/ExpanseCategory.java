package com.amar.fullstack.expanse_tracker_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "name"})
        }
)
public class ExpanseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 Each category belongs to a user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 🔥 NOT globally unique anymore
    @Column(nullable = false)
    private String name;

    private Double totalAmount = 0.0;

    public ExpanseCategory() {}

    public ExpanseCategory(Long id, User user, String name, Double totalAmount) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}