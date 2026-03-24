package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<ExpanseResponseDto> getAll(ExpanseRequestDto dto) {
        List<Expanse> expanses = expRepo.findAll();
        return expanses.stream().map(this::mapToResponse).toList();
    }

    public ExpanseResponseDto getById(Long id) {
        Expanse expanse = expRepo.findById(id).orElseThrow(() -> new RuntimeException("Expanse not find"));
        return mapToResponse(expanse);
    }

    public ExpanseResponseDto updateExpanse(Long id, ExpanseRequestDto dto) {
        Expanse expanse = expRepo.findById(id).orElseThrow(() -> new RuntimeException("Expanse not find"));
        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setCategory(dto.getCategory());
        Expanse updatedExpanse = expRepo.save(expanse);
        return mapToResponse(updatedExpanse);
    }


    private ExpanseResponseDto mapToResponse(Expanse expanse) {

        ExpanseResponseDto responseDto = new ExpanseResponseDto();

        responseDto.setId(expanse.getId());
        responseDto.setName(expanse.getName());
        responseDto.setAmount(expanse.getAmount());
        responseDto.setDescription(expanse.getDescription());
        responseDto.setCategory(expanse.getCategory());
        responseDto.setCreatedAt(expanse.getCreatedAt());
        responseDto.setUpdatedAt(expanse.getUpdatedAt());
        return responseDto;
    }

    private Expanse mapToEntity(ExpanseRequestDto dto) {

        Expanse expanse = new Expanse();

        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setDescription(dto.getDescription());
        expanse.setCategory(dto.getCategory());

        return expanse;
    }
}
