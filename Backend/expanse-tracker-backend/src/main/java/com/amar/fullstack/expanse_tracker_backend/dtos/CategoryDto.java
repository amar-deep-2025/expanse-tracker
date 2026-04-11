package com.amar.fullstack.expanse_tracker_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryDto {

    @NotBlank(message = "Category name is required")
    private String category;

    @NotNull(message = "Amount is required")
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
