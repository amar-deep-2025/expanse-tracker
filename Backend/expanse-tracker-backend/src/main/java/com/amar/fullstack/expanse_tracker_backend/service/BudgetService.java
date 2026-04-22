package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.*;
import com.amar.fullstack.expanse_tracker_backend.entity.*;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.mapping.BudgetMapper;
import com.amar.fullstack.expanse_tracker_backend.notification.service.NotificationService;
import com.amar.fullstack.expanse_tracker_backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ExpanseRepository expanseRepo;
    private final NotificationService notificationService;

    public BudgetService(BudgetRepository budgetRepo,
                         UserRepository userRepo,
                         ExpanseCategoryRepository categoryRepo,
                         BudgetMapper budgetMapper,
                         ExpanseRepository expanseRepo,
                         NotificationService notificationService) {
        this.budgetRepo = budgetRepo;
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.budgetMapper = budgetMapper;
        this.expanseRepo = expanseRepo;
        this.notificationService = notificationService;
    }

    // ✅ CREATE BUDGET
    public BudgetResponseDto createBudget(Long userId, BudgetRequestDto dto) {

        logger.info("Creating budget for userId={}", userId);

        validateYear(dto.getYear());
        validateCategoryRule(dto);

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // 🔥 FIXED duplicate check
        boolean exists;

        if (dto.getType() == BudgetType.OVERALL) {
            exists = budgetRepo.existsByUserIdAndMonthAndYearAndType(
                    userId,
                    dto.getMonth(),
                    dto.getYear(),
                    dto.getType()
            );
        } else {
            exists = budgetRepo.existsByUserIdAndMonthAndYearAndTypeAndCategoryId(
                    userId,
                    dto.getMonth(),
                    dto.getYear(),
                    dto.getType(),
                    dto.getCategoryId()
            );
        }

        logger.info("Budget exists check: {}", exists);

        if (exists) {
            throw new RuntimeException("Budget already exists for this month/year");
        }

        double totalIncome = defaultZero(expanseRepo.getTotalIncome(userId));

        boolean isWarning = dto.getBudget().doubleValue() > totalIncome;

        Budget budget = budgetMapper.toEntity(dto);
        budget.setUser(user);

        // ✅ CATEGORY handling
        if (dto.getType() == BudgetType.CATEGORY) {

            ExpanseCategory category = categoryRepo
                    .findById(dto.getCategoryId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Category not found"));

            if (!category.getUser().getId().equals(userId)) {
                throw new ResourceNotFoundException("Category does not belong to user");
            }

            budget.setCategory(category);
        } else {
            budget.setCategory(null);
        }

        // 🔥 SAVE
        Budget saved = budgetRepo.save(budget);
        logger.info("Budget saved successfully with id={}", saved.getId());

        BudgetResponseDto responseDto = budgetMapper.toDto(saved);

        // 🔥 Notification
        if (isWarning) {
            logger.warn("Budget exceeds income userId={}", userId);
            responseDto.setWarning("Budget exceeds your income");
            sendBudgetAlert(user, dto.getBudget().doubleValue(), totalIncome);
        }

        return responseDto;
    }

    // ✅ GET ALL
    @Transactional(readOnly = true)
    public List<BudgetResponseDto> getAllBudgets(Long userId) {
        return budgetRepo.findByUserIdOrderByYearDescMonthDesc(userId)
                .stream()
                .map(budgetMapper::toDto)
                .toList();
    }

    // ✅ GET BY ID
    @Transactional(readOnly = true)
    public BudgetResponseDto getBudgetById(Long id, Long userId) {
        Budget budget = budgetRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        return budgetMapper.toDto(budget);
    }

    // ✅ DELETE
    public void deleteBudget(Long id, Long userId) {
        Budget budget = budgetRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        budgetRepo.delete(budget);
        logger.info("Budget deleted id={}", id);
    }

    // ✅ UPDATE
    public BudgetResponseDto updateBudget(Long id, Long userId, BudgetRequestDto dto) {

        validateYear(dto.getYear());
        validateCategoryRule(dto);

        Budget budget = budgetRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        budget.setName(dto.getName());
        budget.setBudget(dto.getBudget());
        budget.setMonth(dto.getMonth());
        budget.setYear(dto.getYear());
        budget.setType(dto.getType());

        if (dto.getType() == BudgetType.CATEGORY) {
            ExpanseCategory category = categoryRepo
                    .findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            budget.setCategory(category);
        } else {
            budget.setCategory(null);
        }

        Budget updated = budgetRepo.save(budget);
        logger.info("Budget updated id={}", updated.getId());

        return budgetMapper.toDto(updated);
    }

    // ✅ NOTIFICATION
    private void sendBudgetAlert(User user, double budget, double income) {

        try {
            NotificationRequest notify = new NotificationRequest();

            notify.setEmail(user.getEmail());
            notify.setSubject("Budget Exceeded Alert ⚠");

            notify.setMessage(
                    "Hello " + user.getName() + ",\n\n" +
                            "⚠ Your budget exceeds your income.\n\n" +
                            "Budget: ₹" + budget + "\n" +
                            "Income: ₹" + income + "\n\n" +
                            "Please adjust your budget.\n\n" +
                            "----------------------------------\n" +
                            "This is a system-generated message."
            );

            notify.setTypes(List.of(NotificationType.EMAIL));

            notificationService.send(notify);

            logger.info("Budget alert email sent to {}", user.getEmail());

        } catch (Exception e) {
            logger.error("Failed to send email", e);
        }
    }

    // ✅ VALIDATIONS
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

    private double defaultZero(Double value) {
        return value == null ? 0.0 : value;
    }

    public double getTotalBudget(Long userId) {
        return budgetRepo.getTotalBudget(userId);
    }
}