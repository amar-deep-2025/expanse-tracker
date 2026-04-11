package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.CategoryDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.DashboardSummaryDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.MonthlyDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private static final Logger logger =
            LoggerFactory.getLogger(DashboardService.class);

    private final ExpanseRepository expRepo;

    public DashboardService(ExpanseRepository expRepo) {
        this.expRepo = expRepo;
    }

    public DashboardSummaryDto getSummary(User user) {

        logger.info("Calculating dashboard summary for user: {}", user.getEmail());

        Double expense = defaultZero(expRepo.getTotalExpense(user));
        Double income = defaultZero(expRepo.getTotalIncome(user));
        double balance = income - expense;
        logger.debug("Summary -> income: {}, expense: {}, balance: {}",
                income, expense, balance);
        return new DashboardSummaryDto(expense, income, balance);
    }

    public DashboardSummaryDto getSummaryByDate(
            User user,
            LocalDateTime start,
            LocalDateTime end) {
        logger.info("Calculating summary by date for user: {} from {} to {}",
                user.getEmail(), start, end);
        Double income = defaultZero(
                expRepo.getIncomeBetweenDates(user, start, end));
        Double expense = defaultZero(
                expRepo.getExpenseBetweenDates(user, start, end));
        double balance = income - expense;
        logger.debug("Date Summary -> income: {}, expense: {}, balance: {}",
                income, expense, balance);
        return new DashboardSummaryDto(expense, income, balance);
    }

    public List<CategoryDto> getCategorySummary(
            User user,
            LocalDateTime start,
            LocalDateTime end) {
        logger.info("Fetching category summary for user: {} between {} and {}",
                user.getEmail(), start, end);
        List<Object[]> data = expRepo.getCategorySummary(user, start, end);
        logger.debug("Raw category data size: {}", data.size());
        return data.stream()
                .map(this::mapToCategoryDto)
                .toList();
    }

    public List<MonthlyDto> getMonthly(User user, int year) {

        logger.info("Fetching monthly data for user: {} for year: {}",
                user.getEmail(), year);
        List<Object[]> data = expRepo.getMonthlyIncomeExpense(user, year);
        logger.debug("Monthly raw data size: {}", data.size());
        return data.stream()
                .map(this::mapToMonthlyDto)
                .toList();
    }

    public List<Expanse> getRecentExpenses(User user) {
        logger.info("Fetching recent expenses for user: {}", user.getEmail());
        List<Expanse> expenses =
                expRepo.findTop5ByUserOrderByExpanseDateDesc(user);

        logger.debug("Fetched {} recent expenses", expenses.size());

        return expenses;
    }

    private Double defaultZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private CategoryDto mapToCategoryDto(Object[] obj) {
        return new CategoryDto(
                (String) obj[0],
                obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0
        );
    }

    private MonthlyDto mapToMonthlyDto(Object[] obj) {
        Integer month = (Integer) obj[0];
        double income = obj[1] != null
                ? ((Number) obj[1]).doubleValue()
                : 0.0;
        double expense = obj[2] != null
                ? ((Number) obj[2]).doubleValue()
                : 0.0;
        return new MonthlyDto(month, income, expense);
    }
}