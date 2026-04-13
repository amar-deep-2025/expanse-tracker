package com.amar.fullstack.expanse_tracker_backend.dtos;

public class ComparisonDto {

    private double currentMonthExpenses;
    private double lastMonthExpenses;
    private double difference;
    private double percentageChange;
    private String status;

    public ComparisonDto() {
    }

    public ComparisonDto(double currentMonthExpenses, double lastMonthExpenses, double difference, double percentageChange, String status) {
        this.currentMonthExpenses = currentMonthExpenses;
        this.lastMonthExpenses = lastMonthExpenses;
        this.difference = difference;
        this.percentageChange = percentageChange;
        this.status = status;
    }

    public double getCurrentMonthExpenses() {
        return currentMonthExpenses;
    }

    public void setCurrentMonthExpenses(double currentMonthExpenses) {
        this.currentMonthExpenses = currentMonthExpenses;
    }

    public double getLastMonthExpenses() {
        return lastMonthExpenses;
    }

    public void setLastMonthExpenses(double lastMonthExpenses) {
        this.lastMonthExpenses = lastMonthExpenses;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }

    public double getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(double percentageChange) {
        this.percentageChange = percentageChange;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
