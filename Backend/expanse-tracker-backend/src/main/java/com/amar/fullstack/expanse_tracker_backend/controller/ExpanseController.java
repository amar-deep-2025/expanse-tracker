package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.service.ExpanseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/expanses")
public class ExpanseController {

    private final ExpanseService expService;

    public ExpanseController(ExpanseService expService) {
        this.expService = expService;
    }

    @PostMapping()
    public ExpanseResponseDto create(@Valid @RequestBody ExpanseRequestDto dto) {
        return expService.createExpanse(dto);
    }

    @GetMapping()
    public List<ExpanseResponseDto> findAll() {
        return expService.getAll(null);
    }

    @GetMapping("/{id}")
    public ExpanseResponseDto getById(@PathVariable Long id) {
        ExpanseResponseDto expanseResponseDto = expService.getById(id);
        return expanseResponseDto;
    }
}
