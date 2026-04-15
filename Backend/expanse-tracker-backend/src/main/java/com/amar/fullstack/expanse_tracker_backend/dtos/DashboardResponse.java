package com.amar.fullstack.expanse_tracker_backend.dtos;

import java.util.List;
import java.util.Map;

public class DashboardResponse {

    private Double totalIncome;
    private Double totalBudget;
    private Double balance;
    private Double budgetRemaining;
    private Double totalExpense;
    private Double monthlyExpense;
    private Double todayExpense;
    private Map<String, Double> categorySummary;
    private List<RecentExpanseDto> recentExpenses;


    public DashboardResponse() {
    }

    public DashboardResponse(Double totalIncome,Double totalBudget,Double balance,Double budgetRemaining,Double totalExpense, Double monthlyExpense, Double todayExpense,
            Map<String, Double> categorySummary, List<RecentExpanseDto> recentExpenses) {
        this.totalIncome=totalIncome;
        this.totalBudget=totalBudget;
        this.balance=balance;
        this.budgetRemaining=budgetRemaining;
        this.totalExpense = totalExpense;
        this.monthlyExpense = monthlyExpense;
        this.todayExpense = todayExpense;
        this.categorySummary = categorySummary;
        this.recentExpenses = recentExpenses;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Double getMonthlyExpense() {
        return monthlyExpense;
    }

    public void setMonthlyExpense(Double monthlyExpense) {
        this.monthlyExpense = monthlyExpense;
    }

    public Double getTodayExpense() {
        return todayExpense;
    }

    public void setTodayExpense(Double todayExpense) {
        this.todayExpense = todayExpense;
    }

    public Map<String, Double> getCategorySummary() {
        return categorySummary;
    }

    public void setCategorySummary(Map<String, Double> categorySummary) {
        this.categorySummary = categorySummary;
    }

    public List<RecentExpanseDto> getRecentExpenses() {
        return recentExpenses;
    }

    public void setRecentExpenses(List<RecentExpanseDto> recentExpenses) {
        this.recentExpenses = recentExpenses;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getBudgetRemaining() {
        return budgetRemaining;
    }

    public void setBudgetRemaining(Double budgetRemaining) {
        this.budgetRemaining = budgetRemaining;
    }
}
