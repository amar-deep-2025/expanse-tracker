package com.amar.fullstack.expanse_tracker_backend.dtos;


public class DashboardSummaryDto {

    private Double totalExpense;
    private Double totalIncome;
    private Double balance;

    public DashboardSummaryDto() {
    }

    public DashboardSummaryDto(Double totalExpense, Double totalIncome, Double balance) {
        this.totalExpense = totalExpense;
        this.totalIncome = totalIncome;
        this.balance = balance;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }


}
