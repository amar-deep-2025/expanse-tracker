package com.amar.fullstack.expanse_tracker_backend.dtos;

public class MonthlyDto {

    private Integer month;
    private Double income;
    private Double expense;

    public MonthlyDto() {
    }

    public MonthlyDto(Integer month, Double income, Double expense) {
        this.month = month;
        this.income = income;
        this.expense = expense;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getExpense() {
        return expense;
    }

    public void setExpense(Double expense) {
        this.expense = expense;
    }
}
