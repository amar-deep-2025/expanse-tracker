package com.amar.fullstack.expanse_tracker_backend.controller;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.service.ExpanseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expanses")
public class ExpanseController {

    private final ExpanseService expService;

    public ExpanseController(ExpanseService expService){
        this.expService=expService;
    }

    @PostMapping()
    public ExpanseResponseDto create(@Valid @RequestBody ExpanseRequestDto dto){
        return expService.createExpanse(dto);
    }
}
