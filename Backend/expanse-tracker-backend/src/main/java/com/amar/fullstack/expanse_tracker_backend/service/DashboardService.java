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

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    private final ExpanseRepository expRepo;

    public DashboardService(ExpanseRepository expRepo) {
        this.expRepo = expRepo;
    }

    public DashboardSummaryDto getSummary(User user){
        logger.info("Calculating dashboard summary for user: {}", user.getEmail());

        try {
            Double expense = expRepo.getTotalExpense(user);
            Double income = expRepo.getTotalIncome(user);

            expense = (expense == null) ? 0.0 : expense;
            income = (income == null) ? 0.0 : income;

            double balance = income - expense;

            logger.debug("Summary -> income: {}, expense: {}, balance: {}", income, expense, balance);

            return new DashboardSummaryDto(expense, income, balance);

        } catch (Exception e) {
            logger.error("Error while calculating dashboard summary for user: {}", user.getEmail(), e);
            throw e;
        }
    }

    public DashboardSummaryDto getSummaryByDate(User user, LocalDateTime start, LocalDateTime end){

        logger.info("Calculating summary by date for user: {} from {} to {}", user.getEmail(), start, end);

        try {
            Double income = expRepo.getIncomeBetweenDates(user, start, end);
            Double expense = expRepo.getExpenseBetweenDates(user, start, end);

            income = (income == null) ? 0.0 : income;
            expense = (expense == null) ? 0.0 : expense;

            double balance = income - expense;

            logger.debug("Date Summary -> income: {}, expense: {}, balance: {}", income, expense, balance);

            return new DashboardSummaryDto(expense, income, balance);

        } catch (Exception e) {
            logger.error("Error in getSummaryByDate for user: {}", user.getEmail(), e);
            throw e;
        }
    }

    public List<CategoryDto> getCategorySummary(User user,
                                                LocalDateTime start,
                                                LocalDateTime end){

        logger.info("Fetching category summary for user: {} between {} and {}", user.getEmail(), start, end);

        try {
            List<Object[]> data = expRepo.getCategorySummary(user, start, end);

            logger.debug("Raw category data size: {}", data.size());

            return data.stream().map(obj -> new CategoryDto(
                    (String) obj[0],
                    obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0
            )).toList();

        } catch (Exception e) {
            logger.error("Error while fetching category summary for user: {}", user.getEmail(), e);
            throw e;
        }
    }

    public List<MonthlyDto> getMonthly(User user, int year){

        logger.info("Fetching monthly data for user: {} for year: {}", user.getEmail(), year);

        try {
            List<Object[]> data = expRepo.getMonthlyIncomeExpense(user, year);

            logger.debug("Monthly raw data size: {}", data.size());

            return data.stream().map(obj -> {

                Integer month = (Integer) obj[0];

                double income = obj[1] != null
                        ? ((Number) obj[1]).doubleValue()
                        : 0.0;

                double expense = obj[2] != null
                        ? ((Number) obj[2]).doubleValue()
                        : 0.0;

                return new MonthlyDto(month, income, expense);

            }).toList();

        } catch (Exception e) {
            logger.error("Error while fetching monthly data for user: {}", user.getEmail(), e);
            throw e;
        }
    }

    public List<Expanse> getRecentExpenses(User user){

        logger.info("Fetching recent expenses for user: {}", user.getEmail());

        try {
            List<Expanse> expenses = expRepo.findTop5ByUserOrderByExpanseDateDesc(user);

            logger.debug("Fetched {} recent expenses", expenses.size());

            return expenses;

        } catch (Exception e) {
            logger.error("Error while fetching recent expenses for user: {}", user.getEmail(), e);
            throw e;
        }
    }
}