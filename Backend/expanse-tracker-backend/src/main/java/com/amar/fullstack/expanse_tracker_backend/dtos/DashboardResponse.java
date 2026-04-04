package com.amar.fullstack.expanse_tracker_backend.dtos;

import java.util.List;
import java.util.Map;

public class DashboardResponse {

    private Double totalExpense;
    private Double monthlyExpense;
    private Double todayExpense;
    private Map<String, Double> categorySummary;
    private List<ExpanseResponseDto> recentExpenses;

    public DashboardResponse(){}

    public DashboardResponse(Double totalExpense, Double monthlyExpense, Double todayExpense, Map<String, Double> categorySummary, List<ExpanseResponseDto> recentExpenses) {
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

    public List<ExpanseResponseDto> getRecentExpenses() {
        return recentExpenses;
    }

    public void setRecentExpenses(List<ExpanseResponseDto> recentExpenses) {
        this.recentExpenses = recentExpenses;
    }
}
