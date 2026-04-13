package com.amar.fullstack.expanse_tracker_backend.dtos;

import com.amar.fullstack.expanse_tracker_backend.entity.BudgetType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class BudgetRequestDto {

    @NotBlank(message = "Budget name is required")
    private String name;

    @NotNull(message = "Budget amount is required")
    @DecimalMin("0.01")
    private BigDecimal budget;

    @NotNull(message = "Budget type is required")
    @Min(1)
    @Max(12)
    private Integer month;

    @NotNull(message = "year is required")
    private Integer year;

    @NotNull(message = "type is required")
    private BudgetType type;

    @NotNull(message = "Category ID is required for category budgets")
    private Long categoryId;

    public BudgetRequestDto(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BudgetType getType() {
        return type;
    }

    public void setType(BudgetType type) {
        this.type = type;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
