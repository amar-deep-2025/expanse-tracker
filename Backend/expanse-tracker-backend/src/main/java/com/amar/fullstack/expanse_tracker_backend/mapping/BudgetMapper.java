package com.amar.fullstack.expanse_tracker_backend.mapping;

import com.amar.fullstack.expanse_tracker_backend.dtos.BudgetRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.BudgetResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Budget;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    public BudgetResponseDto toDto(Budget budget) {

        return new BudgetResponseDto(
                budget.getId(),
                budget.getName(),
                budget.getBudget(),
                budget.getMonth(),
                budget.getYear(),
                budget.getType(),
                budget.getCategory() != null
                        ? budget.getCategory().getName()
                        : null
        );
    }

    public Budget toEntity(BudgetRequestDto dto) {

        Budget budget = new Budget();

        budget.setName(dto.getName());
        budget.setBudget(dto.getBudget());
        budget.setMonth(dto.getMonth());
        budget.setYear(dto.getYear());
        budget.setType(dto.getType());

        return budget;
    }
}