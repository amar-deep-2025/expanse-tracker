package com.amar.fullstack.expanse_tracker_backend.service;

import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseRequestDto;
import com.amar.fullstack.expanse_tracker_backend.dtos.ExpanseResponseDto;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.repository.ExpanseRepository;
import org.springframework.stereotype.Service;

@Service
public class ExpanseService {

    private final ExpanseRepository expRepo;

    public ExpanseService(ExpanseRepository expRepo){
        this.expRepo=expRepo;

    }

    public ExpanseResponseDto createExpanse(ExpanseRequestDto dto){

        Expanse expanse=new Expanse();

        expanse.setName(dto.getName());
        expanse.setAmount(dto.getAmount());
        expanse.setCategory(dto.getCategory());
        expanse.setDescription(dto.getDescription());

        Expanse savedExpanse= expRepo.save(expanse);

        ExpanseResponseDto responseDto=new ExpanseResponseDto();
        responseDto.setId(savedExpanse.getId());
        responseDto.setName(savedExpanse.getName());
        responseDto.setAmount(savedExpanse.getAmount());
        responseDto.setCategory(savedExpanse.getCategory());
        responseDto.setDescription(savedExpanse.getDescription());
        responseDto.setCreatedAt(savedExpanse.getCreatedAt());
        responseDto.setUpdatedAt(savedExpanse.getUpdatedAt());

        return responseDto;
    }

}
