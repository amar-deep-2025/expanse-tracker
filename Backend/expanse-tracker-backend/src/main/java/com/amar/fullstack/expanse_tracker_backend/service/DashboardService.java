package com.amar.fullstack.expanse_tracker_backend.service;
import com.amar.fullstack.expanse_tracker_backend.dtos.*;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.entity.Type;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.repository.BudgetRepository;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ExpanseRepository expRepo;
    private final BudgetRepository budgetRepo;

    public DashboardService(ExpanseRepository expRepo, BudgetRepository budgetRepo) {
        this.expRepo = expRepo;
        this.budgetRepo = budgetRepo;
    }

    // 🔥 DEFAULT DASHBOARD
    public DashboardResponse getSummary(User user) {

        Long userId = user.getId();

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        Double income = defaultZero(expRepo.getTotalIncome(userId));
        Double expense = defaultZero(expRepo.getTotalExpense(userId));
        Double budget = defaultZero(budgetRepo.getTotalBudget(userId)); // ✅ FIX

        Double monthlyExpense = defaultZero(
                expRepo.getExpenseByMonth(userId, now.getMonthValue(), now.getYear())
        );

        Double todayExpense = defaultZero(
                expRepo.getExpenseBetweenDates(
                        userId,
                        today.atStartOfDay(),
                        today.atTime(23, 59, 59)
                )
        );

        Map<String, Double> categoryMap = expRepo.getTopCategory(userId)
                .stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> ((Number) obj[1]).doubleValue()
                ));

        List<RecentExpanseDto> recentList = getRecentExpenses(user);

        return new DashboardResponse(
                income,
                budget,                     // ✅ correct order
                income - expense,
                budget - expense,
                expense,
                monthlyExpense,
                todayExpense,
                categoryMap,
                recentList
        );
    }

    // 🔥 DATE FILTER DASHBOARD
    public DashboardResponse getSummaryByDate(
            User user,
            LocalDateTime start,
            LocalDateTime end) {

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        Long userId = user.getId();

        Double income = defaultZero(
                expRepo.getIncomeBetweenDates(userId, start, end));

        Double expense = defaultZero(
                expRepo.getExpenseBetweenDates(userId, start, end));

        Double budget = defaultZero(
                budgetRepo.getBudgetBetweenMonths(
                        userId,
                        start.getMonthValue(),
                        start.getYear(),
                        end.getMonthValue(),
                        end.getYear()
                )
        );

        Map<String, Double> categoryMap =
                expRepo.getCategorySummary(userId, start, end)
                        .stream()
                        .collect(Collectors.toMap(
                                obj -> (String) obj[0],
                                obj -> ((Number) obj[1]).doubleValue()
                        ));

        List<RecentExpanseDto> recentList = getRecentExpenses(user);

        return new DashboardResponse(
                income,
                budget,
                income - expense,
                budget - expense,
                expense,
                expense,     // monthlyExpense approx (date filter case)
                0.0,
                categoryMap,
                recentList
        );
    }

    // 🔹 RECENT EXPENSES
    public List<RecentExpanseDto> getRecentExpenses(User user) {
        return expRepo.findTop5ByUser_IdOrderByExpanseDateDesc(user.getId())
                .stream()
                .map(this::mapToRecentDto)
                .toList();
    }

    public List<RecentExpanseDto> getRecentExpensesByUserId(Long userId) {
        return expRepo.findTop5ByUser_IdOrderByExpanseDateDesc(userId)
                .stream()
                .map(this::mapToRecentDto)
                .toList();
    }


    // 🔹 MONTHLY CHART
    public List<MonthlyDto> getMonthly(User user, int year) {
        return expRepo.getMonthlyIncomeExpense(user.getId(), year)
                .stream()
                .map(this::mapToMonthlyDto)
                .toList();
    }

    public List<MonthlyDto> getMonthly_ByUserId(Long userId, int year) {
        return expRepo.getMonthlyIncomeExpense(userId, year)
                .stream()
                .map(this::mapToMonthlyDto)
                .toList();

    }

    public List<CategoryDto> getCategorySummary(
            User user,
            LocalDateTime start,
            LocalDateTime end) {

        return expRepo.getCategorySummary(user.getId(), start, end)
                .stream()
                .map(this::mapToCategoryDto)
                .toList();
    }

    public List<CategoryDto> getCategorySummary(
            Long userId,
            LocalDateTime start,
            LocalDateTime end) {

        return expRepo.getCategorySummary(userId, start, end)
                .stream()
                .map(this::mapToCategoryDto)
                .toList();
    }

    // 🔹 MONTH COMPARISON (RESTORED ✅)
    public ComparisonDto compareCurrentMonth(Long userId) {

        LocalDateTime now = LocalDateTime.now();

        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        LocalDateTime lastMonthDate = now.minusMonths(1);

        int lastMonth = lastMonthDate.getMonthValue();
        int lastYear = lastMonthDate.getYear();

        double current = defaultZero(
                expRepo.getExpenseByMonth(userId, currentMonth, currentYear)
        );

        double last = defaultZero(
                expRepo.getExpenseByMonth(userId, lastMonth, lastYear)
        );

        double diff = current - last;

        double percent = last == 0 ? 100 : (diff / last) * 100;
        percent = Math.round(percent * 100.0) / 100.0;

        String status = diff > 0 ? "Increased"
                : diff < 0 ? "Decreased"
                : "No Change";

        return new ComparisonDto(
                current,
                last,
                Math.abs(diff),
                Math.abs(percent),
                status
        );
    }

    // 🔹 TOP CATEGORY (RESTORED ✅)
    public CategoryDto getTopCategory(Long userId) {

        List<Object[]> data = expRepo.getTopCategory(userId);

        if (data == null || data.isEmpty()) {
            return new CategoryDto("No Data", 0.0);
        }

        Object[] row = data.get(0);

        return new CategoryDto(
                (String) row[0],
                row[1] != null ? ((Number) row[1]).doubleValue() : 0.0
        );
    }

    public double getTotalAmountByType(User user, Type type) {
        return expRepo.getTotalByType(user.getId(), type);
    }

    public double getTotalIncome(Long userId){
        return expRepo.getTotalIncome(userId);
    }

    public double getTotalExpense(Long userId){
        return expRepo.getTotalExpense(userId);
    }


    // 🔹 COMMON METHODS

    private Double defaultZero(Double value) {
        return value == null ? 0.0 : value; // ✅ FIXED
    }

    private RecentExpanseDto mapToRecentDto(Expanse e) {
        return new RecentExpanseDto(
                e.getId(),
                e.getName(),
                e.getAmount(),
                e.getCategory() != null ? e.getCategory().getName() : null,
                e.getType().name()
        );
    }

    private MonthlyDto mapToMonthlyDto(Object[] obj) {
        return new MonthlyDto(
                (Integer) obj[0],
                obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0,
                obj[2] != null ? ((Number) obj[2]).doubleValue() : 0.0
        );
    }

    private CategoryDto mapToCategoryDto(Object[] obj) {
        return new CategoryDto(
                (String) obj[0],
                obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0
        );
    }

    public List<BudgetResponseDto> getAllBudgets(Long userId){
        return budgetRepo.findByUserId(userId)
                .stream()
                .map(budget -> new BudgetResponseDto(
                        budget.getId(),
                        budget.getName(),
                        budget.getBudget(),
                        budget.getMonth(),
                        budget.getYear(),
                        budget.getType(),
                        budget.getCategory().getName(),
                        null
                ))
                .toList();
    }

    public DashboardResponse getSummaryByUserId(Long userId) {

        User user = new User();
        user.setId(userId);

        return getSummary(user);
    }
}