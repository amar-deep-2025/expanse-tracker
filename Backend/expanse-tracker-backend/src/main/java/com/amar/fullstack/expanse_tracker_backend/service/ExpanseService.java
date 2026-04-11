package com.amar.fullstack.expanse_tracker_backend.service;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.entity.ExpanseCategory;
import com.amar.fullstack.expanse_tracker_backend.entity.Type;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.exception.UnAuthorizedException;
import com.amar.fullstack.expanse_tracker_backend.repository.CategoryRepository;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpanseService {

    private static final Logger logger =
            LoggerFactory.getLogger(ExpanseService.class);

    private final ExpanseRepository expRepo;
    private final CategoryRepository categoryRepo;

    public ExpanseService(ExpanseRepository expRepo,
                          CategoryRepository categoryRepo) {
        this.expRepo = expRepo;
        this.categoryRepo = categoryRepo;
    }

    public ExpanseResponseDto createExpanse(ExpanseRequestDto dto, User user) {

        logger.info("Creating expense for user: {}", user.getEmail());

        ExpanseCategory category = getOrCreateCategory(dto.getCategory());

        category.setTotalAmount(
                safe(category.getTotalAmount()) + dto.getAmount()
        );

        categoryRepo.save(category);

        Expanse expanse = new Expanse();
        expanse.setName(dto.getName());
        expanse.setType(Type.valueOf(dto.getType()));
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setCategory(category);
        expanse.setUser(user);

        Expanse saved = expRepo.save(expanse);

        logger.info("Expense created successfully with id: {}", saved.getId());

        return mapToResponse(saved);
    }

    public List<ExpanseResponseDto> getAll() {

        logger.info("Fetching all expenses");

        List<ExpanseResponseDto> expenses = expRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        logger.info("Total expenses fetched: {}", expenses.size());

        return expenses;
    }

    public ExpanseResponseDto getById(Long id) {

        logger.info("Fetching expense with id: {}", id);

        ExpanseResponseDto response = mapToResponse(findExpenseById(id));

        logger.info("Expense fetched successfully with id: {}", id);

        return response;
    }

    public ExpanseResponseDto getById(Long id, User user) {

        logger.info("Fetching expense id: {} for user: {}", id, user.getEmail());

        Expanse expanse = findExpenseById(id);
        validateOwner(expanse, user);

        logger.info("Expense access granted for id: {}", id);

        return mapToResponse(expanse);
    }

    @Transactional
    public ExpanseResponseDto updateExpanse(Long id,
                                            ExpanseRequestDto dto,
                                            User user) {

        logger.info("Updating expense id: {} for user: {}", id, user.getEmail());

        Expanse expanse = findExpenseById(id);
        validateOwner(expanse, user);

        ExpanseCategory oldCategory = expanse.getCategory();
        ExpanseCategory newCategory = getOrCreateCategory(dto.getCategory());

        if (!oldCategory.getName().equals(newCategory.getName())) {

            logger.info("Category changed from {} to {} for expense id: {}",
                    oldCategory.getName(), newCategory.getName(), id);

            oldCategory.setTotalAmount(
                    safe(oldCategory.getTotalAmount()) - expanse.getAmount()
            );
            categoryRepo.save(oldCategory);

            newCategory.setTotalAmount(
                    safe(newCategory.getTotalAmount()) + dto.getAmount()
            );

        } else {

            double diff = dto.getAmount() - expanse.getAmount();

            logger.info("Amount updated for expense id: {}, diff: {}", id, diff);

            newCategory.setTotalAmount(
                    safe(newCategory.getTotalAmount()) + diff
            );
        }

        categoryRepo.save(newCategory);

        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setCategory(newCategory);

        Expanse updated = expRepo.save(expanse);

        logger.info("Expense updated successfully with id: {}", updated.getId());

        return mapToResponse(updated);
    }

    @Transactional
    public void deleteExpanse(Long id, User user) {

        logger.info("Deleting expense id: {} for user: {}", id, user.getEmail());

        Expanse expanse = findExpenseById(id);
        validateOwner(expanse, user);

        ExpanseCategory category = expanse.getCategory();

        category.setTotalAmount(
                safe(category.getTotalAmount()) - expanse.getAmount()
        );

        categoryRepo.save(category);
        expRepo.delete(expanse);

        logger.info("Expense deleted successfully with id: {}", id);
    }

    public List<ExpanseResponseDto> getUserExpenses(User user) {

        logger.info("Fetching expenses for user: {}", user.getEmail());

        List<ExpanseResponseDto> expenses = expRepo.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

        logger.info("Total expenses fetched for user {}: {}",
                user.getEmail(), expenses.size());

        return expenses;
    }

    private Expanse findExpenseById(Long id) {

        logger.debug("Searching expense in database with id: {}", id);

        return expRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found with id: " + id
                        ));
    }

    private void validateOwner(Expanse expanse, User user) {

        if (!expanse.getUser().getId().equals(user.getId())) {

            logger.warn("Unauthorized access attempt by user: {} for expense id: {}",
                    user.getEmail(), expanse.getId());

            throw new UnAuthorizedException("Unauthorized access");
        }
    }

    private Double safe(Double value) {
        return value == null ? 0.0 : value;
    }

    private ExpanseCategory getOrCreateCategory(String name) {

        logger.info("Fetching category: {}", name);

        return categoryRepo.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    logger.info("Category not found, creating new category: {}", name);

                    ExpanseCategory category = new ExpanseCategory();
                    category.setName(name);

                    return categoryRepo.save(category);
                });
    }

    private ExpanseResponseDto mapToResponse(Expanse expanse) {

        ExpanseResponseDto dto = new ExpanseResponseDto();

        dto.setId(expanse.getId());
        dto.setName(expanse.getName());
        dto.setType(expanse.getType().name());
        dto.setAmount(expanse.getAmount());
        dto.setDescription(expanse.getDescription());
        dto.setCategory(expanse.getCategory().getName());
        dto.setCreatedAt(expanse.getCreatedAt());
        dto.setUpdatedAt(expanse.getUpdatedAt());

        return dto;
    }
}