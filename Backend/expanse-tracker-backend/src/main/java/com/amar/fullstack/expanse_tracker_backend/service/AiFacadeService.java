package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.DashboardResponse;
import org.springframework.stereotype.Service;

@Service
public class AiFacadeService {

    private final AiService aiService;

    public AiFacadeService(AiService aiService) {
        this.aiService = aiService;
    }

    public String generateInsight(DashboardResponse data) {

        double income = data.getTotalIncome();
        double expense = data.getTotalExpense();
        double savings = income - expense;

        return "Income is %.0f, expense is %.0f, and savings are %.0f."
                .formatted(income, expense, savings);
    }

}