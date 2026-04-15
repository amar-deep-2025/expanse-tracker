package com.amar.fullstack.expanse_tracker_backend.mapping;
import com.amar.fullstack.expanse_tracker_backend.dtos.DashboardResponse;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.RecentExpanseDto;

import java.util.List;
import java.util.Map;

public class DashboardMapper {

    public static DashboardResponse toDto(
            Double income,
            Double expense,
            Double budget,
            Double monthlyExpense,
            Double todayExpense,
            Map<String, Double> categorySummary,
            List<RecentExpanseDto> recentExpenses
    ) {

        return new DashboardResponse(
                income,
                expense,
                budget,
                income - expense,
                budget - expense,
                monthlyExpense,
                todayExpense,
                categorySummary,
                recentExpenses
        );
    }
}
