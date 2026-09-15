package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseCategoryResponseDto;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpanseCategoryService {

    private final ExpanseCategoryRepository expanseCategoryRepository;

    public ExpanseCategoryService(ExpanseCategoryRepository expanseCategoryRepository) {
        this.expanseCategoryRepository = expanseCategoryRepository;
    }
    public List<ExpanseCategoryResponseDto> getAllCategories(Long userId) {

        return expanseCategoryRepository.findByUser_Id(userId)
                .stream()
                .map(expanseCategory -> {
                    ExpanseCategoryResponseDto dto =
                            new ExpanseCategoryResponseDto();

                    dto.setId(expanseCategory.getId());
                    dto.setName(expanseCategory.getName());

                    return dto;
                })
                .toList();
    }
}
