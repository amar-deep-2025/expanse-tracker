package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.BudgetRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.BudgetResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Budget;
import com.amar.fullstack.expanse_tracker_backend.entity.BudgetType;
import com.amar.fullstack.expanse_tracker_backend.entity.ExpanseCategory;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.mapping.BudgetMapper;
import com.amar.fullstack.expanse_tracker_backend.repository.*;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@Transactional
public class BudgetService {

    private static final Logger logger =
            LoggerFactory.getLogger(BudgetService.class);

    private final BudgetRepository budgetRepo;
    private final UserRepository userRepo;
    private final ExpanseCategoryRepository categoryRepo;
    private final BudgetMapper budgetMapper;


    public BudgetService(BudgetRepository budgetRepo,
                         UserRepository userRepo,
                         ExpanseCategoryRepository categoryRepo,
                         BudgetMapper budgetMapper) {
        this.budgetRepo = budgetRepo;
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.budgetMapper = budgetMapper;
    }


    public BudgetResponseDto createBudget(Long userId, BudgetRequestDto dto) {

        logger.info("Creating budget for userId={}", userId);

        validateYear(dto.getYear());
        validateCategoryRule(dto);

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        boolean exists =
                budgetRepo.existsByUserIdAndMonthAndYearAndTypeAndCategoryId(
                        userId,
                        dto.getMonth(),
                        dto.getYear(),
                        dto.getType(),
                        dto.getCategoryId()
                );

        if (exists) {
            throw new RuntimeException("Budget already exists for this month/year");
        }

        Budget budget = budgetMapper.toEntity(dto);
        budget.setUser(user);

        if (dto.getType() == BudgetType.CATEGORY) {

            ExpanseCategory category = categoryRepo
                    .findById(dto.getCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Category not found"));
            if (!category.getUser().getId().equals(userId)) {
                throw new ResourceNotFoundException("Category does not belong to user");
            }

            budget.setCategory(category);
        }

        Budget saved = budgetRepo.save(budget);

        return budgetMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponseDto> getAllBudgets(Long userId) {
        logger.info("Fetching all budgets for userId={}", userId);
        return budgetRepo.findByUserIdOrderByYearDescMonthDesc(userId)
                .stream()
                .map(budgetMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public BudgetResponseDto getBudgetById(Long id, Long userId) {
        logger.info("Fetching budget id={} for userId={}", id, userId);
        Budget budget = budgetRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        return budgetMapper.toDto(budget);
    }

    public void deleteBudget(Long id, Long userId) {
        logger.info("Deleting budget id={} for userId={}", id, userId);
        Budget budget = budgetRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found")); budgetRepo.delete(budget);
    }

    public BudgetResponseDto updateBudget(Long id, Long userId, BudgetRequestDto dto) {
        logger.info("Updating budget id={} for userId={}", id, userId);
        validateYear(dto.getYear()); validateCategoryRule(dto);
        Budget budget = budgetRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        budget.setName(dto.getName());
        budget.setBudget(dto.getBudget());
        budget.setMonth(dto.getMonth());
        budget.setYear(dto.getYear());
        budget.setType(dto.getType());
        if (dto.getType() == BudgetType.CATEGORY) {
            ExpanseCategory category = categoryRepo .findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            budget.setCategory(category);
        } else {
            budget.setCategory(null);
        }
        Budget updated = budgetRepo.save(budget);
        return budgetMapper.toDto(updated);
    }

    private void validateCategoryRule(BudgetRequestDto dto) {

        if (dto.getType() == BudgetType.CATEGORY
                && dto.getCategoryId() == null) {

            throw new ResourceNotFoundException(
                    "Category is required for CATEGORY budget"
            );
        }
    }
    private void validateYear(Integer year) {
        int minYear = 2000;
        int currentYear = java.time.Year.now().getValue();
        int maxYear = currentYear + 15;

        if (year < minYear || year > maxYear) {
            throw new IllegalArgumentException(
                    "Please insert year between " + minYear + " and " + maxYear
            );
        }
    }
}