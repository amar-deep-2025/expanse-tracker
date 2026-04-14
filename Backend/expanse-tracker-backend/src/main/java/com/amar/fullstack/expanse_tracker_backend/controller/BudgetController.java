package com.amar.fullstack.expanse_tracker_backend.controller;


import com.amar.fullstack.expanse_tracker_backend.dtos.BudgetRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.BudgetResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetResponseDto> createBudget(
            Authentication auth,
            @RequestBody BudgetRequestDto requestDto
    ) {
        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                budgetService.createBudget(user.getId(), requestDto)
        );
    }


    @GetMapping
    public ResponseEntity<List<BudgetResponseDto>> getAllBudgets(
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                budgetService.getAllBudgets(user.getId())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDto> getBudgetById(
            @PathVariable Long id,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                budgetService.getBudgetById(id, user.getId())
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponseDto> updateBudget(
            @PathVariable Long id,
            Authentication auth,
            @RequestBody BudgetRequestDto dto
    ) {
        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(
                budgetService.updateBudget(id, user.getId(), dto)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        budgetService.deleteBudget(id, user.getId());

        return ResponseEntity.noContent().build();
    }
}