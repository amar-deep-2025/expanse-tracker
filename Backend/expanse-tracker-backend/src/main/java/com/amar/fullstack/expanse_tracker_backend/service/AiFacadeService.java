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

        if (data.getCategorySummary() != null
                && !data.getCategorySummary().isEmpty()) {

            String highestCategory = data.getCategorySummary()
                    .entrySet()
                    .stream()
                    .max(java.util.Map.Entry.comparingByValue())
                    .map(java.util.Map.Entry::getKey)
                    .orElse(null);

            Double highestAmount = highestCategory != null
                    ? data.getCategorySummary().get(highestCategory)
                    : 0.0;

            if (highestCategory != null && highestAmount > 0) {

                String categoryName = highestCategory.substring(0, 1).toUpperCase()
                        + highestCategory.substring(1);

                return String.format(
                        "Your highest expense category is %s at ₹%.0f, so reviewing this category could help improve your savings.",
                        categoryName,
                        highestAmount
                );
            }
        }

        if (balance > 0) {
            return String.format(
                    "You currently have a positive balance of ₹%.0f, so maintaining your spending within your budget can help preserve your savings.",
                    balance
            );
        }

        return "Reviewing your recent expenses and budget can help you manage your finances more effectively.";
    }
    private String cleanInsight(String result) {

        String insight = result.trim();

        // Remove markdown
        insight = insight
                .replace("**", "")
                .replace("```", "")
                .trim();

        // If model still returns multiple lines,
        // keep only the first meaningful line.
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

        // Remove accidental quotation marks
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