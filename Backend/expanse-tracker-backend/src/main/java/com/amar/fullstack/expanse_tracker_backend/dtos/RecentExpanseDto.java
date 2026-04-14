package com.amar.fullstack.expanse_tracker_backend.dtos;

public class RecentExpanseDto {

    private Long id;
    private String name;
    private Double amount;
    private String category;
    private String type;

    public RecentExpanseDto(Long id, String name, Double amount, String category, String type) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.type = type;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    // getters
}
