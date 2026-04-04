package com.amar.fullstack.expanse_tracker_backend.dtos;

public class CategoryDto {

    private String category;
    private Double amount;

    public CategoryDto(){}

    public CategoryDto(String category, Double amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
