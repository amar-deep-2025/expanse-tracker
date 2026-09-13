package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseCategoryResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.ExpanseCategoryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ExpanseCategoryService expanseCategoryService;

    public CategoryController(ExpanseCategoryService expanseCategoryService){
        this.expanseCategoryService=expanseCategoryService;
    }
    @GetMapping
    public List<ExpanseCategoryResponseDto> getAll(Authentication auth){
        User user=(User) auth.getPrincipal();
        return expanseCategoryService.getAllCategories(user.getId());
    }

}
