package com.amar.fullstack.expanse_tracker_backend.repository;

import com.amar.fullstack.expanse_tracker_backend.entity.ExpanseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpanseCategoryRepo extends JpaRepository<ExpanseCategory, Long> {

    List<ExpanseCategory> findByUserId(Long userId);

    boolean existsByNameIgnoreCaseAndUserId(
            String name,
            Long userId
    );
}
