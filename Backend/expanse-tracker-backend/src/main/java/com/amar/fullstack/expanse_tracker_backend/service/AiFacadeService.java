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

    public String generateCustomAnswer(
            String prompt,
            DashboardResponse dashboardData
    ) {

        String data = """
            Financial Dashboard Data:

            Total Income: %s
            Total Budget: %s
            Balance: %s
            Budget Remaining: %s
            Total Expense: %s
            Monthly Expense: %s
            Today's Expense: %s
            Category Summary: %s
            Recent Expenses: %s
            """.formatted(
                dashboardData.getTotalIncome(),
                dashboardData.getTotalBudget(),
                dashboardData.getBalance(),
                dashboardData.getBudgetRemaining(),
                dashboardData.getTotalExpense(),
                dashboardData.getMonthlyExpense(),
                dashboardData.getTodayExpense(),
                dashboardData.getCategorySummary(),
                dashboardData.getRecentExpenses()
        );

        String finalPrompt = """
            You are a financial assistant.

            Use ONLY the following financial dashboard data
            to answer the user's question.

            %s

            User Question:
            %s

            Rules:
            - Do not invent financial data.
            - Do not use external or general knowledge for financial answers.
            - If the provided dashboard data is insufficient,
              clearly say that the required information is unavailable.
            - Give a short and clear answer.
            """.formatted(data, prompt);

        return aiService.ask(finalPrompt);
    }
}