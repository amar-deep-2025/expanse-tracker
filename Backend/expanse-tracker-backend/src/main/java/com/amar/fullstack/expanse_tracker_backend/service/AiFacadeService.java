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

        Double totalExpense = data.getTotalExpense();
        Double totalIncome = data.getTotalIncome();
        Double balance = data.getBalance();


        if (totalExpense == null || totalIncome == null || balance == null) {
            return "Your financial insight is currently unavailable.";
        }

        return String.format("Your income is ₹%.0f | Your Expense is ₹%.0f | Your Balance is ₹%.0f", totalIncome, totalExpense, balance);
    }
    private String cleanInsight(String result) {

        String insight = result.trim();

        insight = insight
                .replace("**", "")
                .replace("```", "")
                .trim();

        String[] lines = insight.split("\\r?\\n");

        for (String line : lines) {

            String cleanLine = line.trim();

            if (!cleanLine.isEmpty()
                    && !cleanLine.startsWith("The user")
                    && !cleanLine.startsWith("I need to")
                    && !cleanLine.startsWith("Let me")
                    && !cleanLine.startsWith("We need to")
                    && !cleanLine.startsWith("Data:")) {

                insight = cleanLine;
                break;
            }
        }

        insight = insight
                .replaceAll("^\"|\"$", "")
                .trim();

        return insight.isEmpty()
                ? "AI insight unavailable"
                : insight;
    }

    public String generateCustomAnswer(
            String prompt,
            DashboardResponse dashboardData
    ) {
        String question=prompt.toLowerCase().trim();

        if (question.contains("total expense")|| question.contains("how much did i spend")){
            return String.format("Your total expenses are ₹%.2f.", dashboardData.getTotalExpense());
        }
        if(question.contains("total income")|| question.contains("how much did i earn")||question.contains("what is my total income")){
            return String.format("Your total income is ₹%.2f.", dashboardData.getTotalIncome());
        }
        if (question.contains("total budget") || question.contains("how much my total budget") || question.contains("what is my total budget")){
            return String.format("Your total budget is ₹%.2f.", dashboardData.getTotalBudget());
        }
        if (question.contains("budget remaining") || question.contains("What is my remaining budget")){
            return String.format("Your remaining budget is ₹%.2f.", dashboardData.getBudgetRemaining());
        }
        if (question.contains("today's expense") || question.contains("how much did i spend today")||question.contains("what is my today's expense")){
            return String.format("Your today's expenses are ₹%.2f.", dashboardData.getTodayExpense());
        }


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