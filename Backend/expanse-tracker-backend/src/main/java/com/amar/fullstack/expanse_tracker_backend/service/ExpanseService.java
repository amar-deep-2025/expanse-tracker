package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import org.springframework.stereotype.Service;

@Service
public class ExpanseService {

    private final ExpanseRepository expRepo;

    public ExpanseService(ExpanseRepository expRepo) {
        this.expRepo = expRepo;

    }

    public ExpanseResponseDto createExpanse(ExpanseRequestDto dto) {

        Expanse expanse = new Expanse();

        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setCategory(dto.getCategory());
        expanse.setDescription(dto.getDescription());

        Expanse savedExpanse = expRepo.save(expanse);

        return mapToResponse(savedExpanse);
    }

    private ExpanseResponseDto mapToResponse(Expanse expanse) {

        ExpanseResponseDto responseDto = new ExpanseResponseDto();

        responseDto.setId(expanse.getId());
        responseDto.setName(expanse.getName());
        responseDto.setAmount(expanse.getAmount());
        responseDto.setCategory(expanse.getCategory());
        responseDto.setCreatedAt(expanse.getCreatedAt());
        responseDto.setUpdatedAt(expanse.getUpdatedAt());
        return responseDto;
    }

    private Expanse mapToEntity(ExpanseRequestDto dto) {

        Expanse expanse = new Expanse();

        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setCategory(dto.getCategory());
        expanse.setCategory(dto.getCategory());
        return expanse;
    }
}
