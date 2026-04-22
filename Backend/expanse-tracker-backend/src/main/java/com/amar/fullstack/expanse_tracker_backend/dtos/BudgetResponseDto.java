package com.amar.fullstack.expanse_tracker_backend.dtos;

import com.amar.fullstack.expanse_tracker_backend.entity.BudgetType;

import java.math.BigDecimal;

public class BudgetResponseDto {
    private Long id;
    private String name;
    private BigDecimal budget;
    private Integer month;
    private Integer year;
    private BudgetType type;
    private String categoryName;
    private String warning;

    public BudgetResponseDto(Long id,
                             String name,
                             BigDecimal budget,
                             Integer month,
                             Integer year,
                             BudgetType type,
                             String categoryName,
                             String warning) {
        this.id = id;
        this.name = name;
        this.budget = budget;
        this.month = month;
        this.year = year;
        this.type = type;
        this.categoryName = categoryName;
        this.warning= warning;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }

    public BudgetType getType() {
        return type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String isWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }
}

