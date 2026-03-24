package com.amar.fullstack.expanse_tracker_backend.entity;

import jakarta.persistence.*;


@Entity
@Table(name="categories")
public class ExpanseCategory {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    private Double totalAmount;

    public ExpanseCategory(){}
    public ExpanseCategory(Long id, String name, Double totalAmount) {
        this.id = id;
        this.name = name;
        this.totalAmount = totalAmount;
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

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
