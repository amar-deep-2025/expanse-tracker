package com.amar.fullstack.expanse_tracker_backend.service;
import com.amar.fullstack.expanse_tracker_backend.dtos.CategoryDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.DashboardSummaryDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.MonthlyDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final ExpanseRepository expRepo;

    public DashboardService(ExpanseRepository expRepo) {
        this.expRepo = expRepo;
    }

    public DashboardSummaryDto getSummary(User user){

        Double expense = expRepo.getTotalExpense(user);
        Double income = expRepo.getTotalIncome(user);

        if (expense == null) expense = 0.0;
        if (income == null) income = 0.0;

        return new DashboardSummaryDto(
                expense,
                income,
                income - expense
        );
    }


    public DashboardSummaryDto getSummaryByDate(User user, LocalDateTime start, LocalDateTime end){

        Double income = expRepo.getIncomeBetweenDates(user, start, end);
        Double expense = expRepo.getExpenseBetweenDates(user, start, end);

        if (income == null) income = 0.0;
        if (expense == null) expense = 0.0;

        return new DashboardSummaryDto(
                expense,
                income,
                income - expense
        );
    }

    public List<CategoryDto> getCategorySummary(User user,
                                                LocalDateTime start,
                                                LocalDateTime end){

        List<Object[]> data = expRepo.getCategorySummary(user, start, end);

        return data.stream().map(obj -> new CategoryDto(
                (String) obj[0],
                obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0
        )).toList();
    }

    public List<MonthlyDto> getMonthly(User user, int year){

        List<Object[]> data = expRepo.getMonthlyIncomeExpense(user, year);

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
    }

    public List<Expanse> getRecentExpenses(User user){
        return expRepo.findTop5ByUserOrderByExpanseDateDesc(user);
    }
}