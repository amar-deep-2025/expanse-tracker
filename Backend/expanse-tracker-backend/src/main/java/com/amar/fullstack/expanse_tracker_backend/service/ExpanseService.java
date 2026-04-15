package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.*;
import com.amar.fullstack.expanse_tracker_backend.exception.ResourceNotFoundException;
import com.amar.fullstack.expanse_tracker_backend.exception.UnAuthorizedException;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseCategoryRepository;
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
    private final ExpanseCategoryRepository categoryRepo;

    public ExpanseService(ExpanseRepository expRepo,
                          ExpanseCategoryRepository categoryRepo) {
        this.expRepo = expRepo;
        this.categoryRepo = categoryRepo;
    }

    public ExpanseResponseDto createExpanse(ExpanseRequestDto dto, User user) {

        ExpanseCategory category;
        if (dto.getCategoryId() != null) {
            category = getCategoryById(dto.getCategoryId(), user);

        } else if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {

            category = categoryRepo
                    .findByNameIgnoreCaseAndUser_Id(dto.getCategoryName(), user.getId())
                    .orElseGet(() -> {
                        ExpanseCategory newCat = new ExpanseCategory();
                        newCat.setName(dto.getCategoryName().trim());
                        newCat.setUser(user);
                        return categoryRepo.save(newCat);
                    });

        } else {
            throw new IllegalArgumentException("Category is required");
        }

        // ✅ update total
        category.setTotalAmount(
                safe(category.getTotalAmount()) + dto.getAmount()
        );

        // ✅ create expense
        Expanse expanse = new Expanse();
        expanse.setName(dto.getName());
        expanse.setType(Type.valueOf(dto.getType()));
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setCategory(category);
        expanse.setUser(user);

        return mapToResponse(expRepo.save(expanse));
    }

    public List<ExpanseResponseDto> getUserExpenses(User user) {

        return expRepo.findByUser_Id(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ExpanseResponseDto getById(Long id, User user) {

        Expanse expanse = findExpenseById(id);
        validateOwner(expanse, user);

        return mapToResponse(expanse);
    }


    private ExpanseCategory resolveCategory(ExpanseRequestDto dto, User user) {
        if (dto.getCategoryId() != null) {
            return getCategoryById(dto.getCategoryId(), user);

        } else if (dto.getCategoryName() != null && !dto.getCategoryName().isBlank()) {

            return categoryRepo
                    .findByNameIgnoreCaseAndUser_Id(dto.getCategoryName(), user.getId())
                    .orElseGet(() -> {
                        ExpanseCategory newCat = new ExpanseCategory();
                        newCat.setName(dto.getCategoryName().trim());
                        newCat.setUser(user);
                        return categoryRepo.save(newCat);
                    });
        }

        throw new IllegalArgumentException("Category is required");
    }

    public ExpanseResponseDto updateExpanse(
            Long id,
            ExpanseRequestDto dto,
            User user) {

        Expanse expanse = findExpenseById(id);
        validateOwner(expanse, user);

        ExpanseCategory oldCategory = expanse.getCategory();
        ExpanseCategory newCategory = resolveCategory(dto, user);
        if (!oldCategory.getId().equals(newCategory.getId())) {

            oldCategory.setTotalAmount(
                    safe(oldCategory.getTotalAmount()) - expanse.getAmount()
            );

            newCategory.setTotalAmount(
                    safe(newCategory.getTotalAmount()) + dto.getAmount()
            );

        } else {
            double diff = dto.getAmount() - expanse.getAmount();

            newCategory.setTotalAmount(
                    safe(newCategory.getTotalAmount()) + diff
            );
        }

        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setType(Type.valueOf(dto.getType()));
        expanse.setCategory(newCategory);

        return mapToResponse(expRepo.save(expanse));
    }

    @Transactional
    public void deleteExpanse(Long id, User user) {

        Expanse expanse = findExpenseById(id);
        validateOwner(expanse, user);

        ExpanseCategory category = expanse.getCategory();

        if (category != null) {
            category.setTotalAmount(
                    safe(category.getTotalAmount()) - expanse.getAmount()
            );
        }
        expRepo.delete(expanse);
    }
    private ExpanseCategory getCategoryById(Long categoryId, User user) {

        return categoryRepo.findById(categoryId)
                .filter(cat -> cat.getUser().getId().equals(user.getId()))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));
    }

    private Expanse findExpenseById(Long id) {
        return expRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found"));
    }

    private void validateOwner(Expanse expanse, User user) {
        if (!expanse.getUser().getId().equals(user.getId())) {
            throw new UnAuthorizedException("Unauthorized access");
        }
    }

    private Double safe(Double value) {
        return value == null ? 0.0 : value;
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