package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.ComparisonDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.ExpanseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expanses")
public class ExpanseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpanseController.class);

    private final ExpanseService expService;

    public ExpanseController(ExpanseService expService) {
        this.expService = expService;
    }

    @PostMapping
    public ResponseEntity<ExpanseResponseDto> create(
            @Valid @RequestBody ExpanseRequestDto dto,
            Authentication auth) {

        logger.info("Create expense API called");

        User user = (User) auth.getPrincipal();

        logger.debug("Expense request received with type: {}", dto.getType());

        return ResponseEntity.ok(expService.createExpanse(dto, user));
    }

    @GetMapping
    public ResponseEntity<List<ExpanseResponseDto>> findAll(Authentication auth) {

        logger.info("Get all expenses API called");

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(expService.getUserExpenses(user));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ExpanseResponseDto> getById(
            @PathVariable Long id,
            Authentication auth) {
        logger.info("Get expense by id API called: {}", id);
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(expService.getById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpanseResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ExpanseRequestDto dto,
            Authentication auth) {
        logger.info("Update expense API called for id: {}", id);
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(expService.updateExpanse(id, dto, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication auth) {
        logger.info("Delete expense API called for id: {}", id);
        User user = (User) auth.getPrincipal();
        expService.deleteExpanse(id, user);
        logger.info("Expense deleted successfully for id: {}", id);

        return ResponseEntity.noContent().build();
    }


}