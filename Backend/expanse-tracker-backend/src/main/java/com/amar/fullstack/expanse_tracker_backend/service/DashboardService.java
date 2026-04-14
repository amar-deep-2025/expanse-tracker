package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.*;
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

        Long userId = user.getId();

        Double expense = defaultZero(expRepo.getTotalExpense(userId));
        Double income = defaultZero(expRepo.getTotalIncome(userId));

        return new DashboardSummaryDto(expense, income, income - expense);
    }

    public DashboardSummaryDto getSummaryByDate(
            User user,
            LocalDateTime start,
            LocalDateTime end) {

        Long userId = user.getId();

        Double income = defaultZero(
                expRepo.getIncomeBetweenDates(userId, start, end));

        Double expense = defaultZero(
                expRepo.getExpenseBetweenDates(userId, start, end));

        return new DashboardSummaryDto(expense, income, income - expense);
    }

    public List<CategoryDto> getCategorySummary(
            User user,
            LocalDateTime start,
            LocalDateTime end) {

        Long userId = user.getId();

        return expRepo.getCategorySummary(userId, start, end)
                .stream()
                .map(this::mapToCategoryDto)
                .toList();
    }

    public List<MonthlyDto> getMonthly(User user, int year) {

        Long userId = user.getId();

        return expRepo.getMonthlyIncomeExpense(userId, year)
                .stream()
                .map(this::mapToMonthlyDto)
                .toList();
    }

    public List<RecentExpanseDto> getRecentExpenses(User user) {

        return expRepo.findTop5ByUser_IdOrderByExpanseDateDesc(user.getId())
                .stream()
                .map(e -> new RecentExpanseDto(
                        e.getId(),
                        e.getName(),
                        e.getAmount(),
                        e.getCategory().getName(),
                        e.getType().name()
                ))
                .toList();
    }
    public ComparisonDto compareCurrentMonth(Long userId){

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
                : diff < 0 ? "Decreased" : "No Change";

        return new ComparisonDto(
                current, last,
                Math.abs(diff),
                Math.abs(percent),
                status
        );
    }

    public CategoryDto getTopCategory(Long userId){

        List<Object[]> data = expRepo.getTopCategory(userId);

        if (data.isEmpty()){
            return new CategoryDto("No Data", 0.0);
        }

        Object[] row = data.get(0);

        return new CategoryDto(
                (String) row[0],
                row[1] != null ? ((Number) row[1]).doubleValue() : 0.0
        );
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
        return new MonthlyDto(
                (Integer) obj[0],
                obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0,
                obj[2] != null ? ((Number) obj[2]).doubleValue() : 0.0
        );
    }
}