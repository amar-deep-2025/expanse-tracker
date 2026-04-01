package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import com.amar.fullstack.expanse_tracker_backend.service.ExpanseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expanses")
public class ExpanseController {

    private final ExpanseService expService;

    public ExpanseController(ExpanseService expService) {
        this.expService = expService;
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ExpanseResponseDto> create(
            @Valid @RequestBody ExpanseRequestDto dto,
            Authentication auth) {

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(expService.createExpanse(dto, user));
    }

    // ================= GET ALL (USER BASED) =================
    @GetMapping
    public ResponseEntity<List<ExpanseResponseDto>> findAll(Authentication auth) {

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(expService.getUserExpenses(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpanseResponseDto> getById(
            @PathVariable Long id,
            Authentication auth) {

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(expService.getById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpanseResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ExpanseRequestDto dto,
            Authentication auth) {

        User user = (User) auth.getPrincipal();

        return ResponseEntity.ok(expService.updateExpanse(id, dto, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication auth) {

        User user = (User) auth.getPrincipal();

        expService.deleteExpanse(id, user);

        return ResponseEntity.noContent().build();
    }
}