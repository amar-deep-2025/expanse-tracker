package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.entity.ExpanseCategory;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.repository.CategoryRepository;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpanseService {

    private final ExpanseRepository expRepo;
    private final CategoryRepository categoryRepo;

    public ExpanseService(ExpanseRepository expRepo,
                          CategoryRepository categoryRepo) {
        this.expRepo = expRepo;
        this.categoryRepo = categoryRepo;
    }

    public ExpanseResponseDto createExpanse(ExpanseRequestDto dto, User user){

        ExpanseCategory category = getOrCreateCategory(dto.getCategory());

        category.setTotalAmount(safe(category.getTotalAmount()) + dto.getAmount());
        categoryRepo.save(category);

        Expanse expanse = new Expanse();
        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setCategory(category);
        expanse.setUser(user);


        return mapToResponse(expRepo.save(expanse));
    }
    public List<ExpanseResponseDto> getAll() {
        return expRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ExpanseResponseDto getById(Long id) {
        Expanse expanse = expRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Expanse not found"));
        return mapToResponse(expanse);
    }

    @Transactional
    public ExpanseResponseDto updateExpanse(Long id, ExpanseRequestDto dto, User user) {

        Expanse expanse = expRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Expanse not found"));


        if (!expanse.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this expense");
        }
        ExpanseCategory oldCategory = expanse.getCategory();
        ExpanseCategory newCategory = getOrCreateCategory(dto.getCategory());
        if (!oldCategory.getName().equals(newCategory.getName())) {

            oldCategory.setTotalAmount(
                    safe(oldCategory.getTotalAmount()) - expanse.getAmount()
            );
            categoryRepo.save(oldCategory);

            newCategory.setTotalAmount(
                    safe(newCategory.getTotalAmount()) + dto.getAmount()
            );
        } else {
            double diff = dto.getAmount() - expanse.getAmount();

            newCategory.setTotalAmount(
                    safe(newCategory.getTotalAmount()) + diff
            );
        }
        categoryRepo.save(newCategory);
        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setCategory(newCategory);

        return mapToResponse(expRepo.save(expanse));
    }

    @Transactional
    public void deleteExpanse(Long id,User user){

        Expanse expanse = expRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Expanse not found"));

        if (!expanse.getUser().getId().equals(user.getId())){
            throw new RuntimeException("You are not authorized to delete this expense");
        }
        ExpanseCategory category = expanse.getCategory();

        category.setTotalAmount(
                safe(category.getTotalAmount()) - expanse.getAmount()
        );
        categoryRepo.save(category);

        expRepo.delete(expanse);
    }

    private Double safe(Double value){
        return value == null ? 0.0 : value;
    }

    private ExpanseCategory getOrCreateCategory(String name){
        return categoryRepo.findByName(name)
                .orElseGet(() -> {
                    ExpanseCategory cat = new ExpanseCategory();
                    cat.setName(name);
                    return categoryRepo.save(cat);
                });
    }
    public ExpanseResponseDto getById(Long id, User user) {

        Expanse expanse = expRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Expanse not found"));

        if (!expanse.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        return mapToResponse(expanse);
    }

    private ExpanseResponseDto mapToResponse(Expanse expanse) {

        ExpanseResponseDto dto = new ExpanseResponseDto();

        dto.setId(expanse.getId());
        dto.setName(expanse.getName());
        dto.setAmount(expanse.getAmount());
        dto.setDescription(expanse.getDescription());
        dto.setCategory(expanse.getCategory().getName());
        dto.setCreatedAt(expanse.getCreatedAt());
        dto.setUpdatedAt(expanse.getUpdatedAt());

        return dto;
    }

    public List<ExpanseResponseDto> getUserExpenses(User user) {
        return expRepo.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}