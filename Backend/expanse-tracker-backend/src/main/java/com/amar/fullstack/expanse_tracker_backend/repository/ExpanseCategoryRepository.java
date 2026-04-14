package com.amar.fullstack.expanse_tracker_backend.repository;
import com.amar.fullstack.expanse_tracker_backend.entity.ExpanseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpanseCategoryRepository extends JpaRepository<ExpanseCategory, Long> {

    List<ExpanseCategory> findByUser_Id(Long userId);

    boolean existsByNameIgnoreCaseAndUser_Id(String name, Long userId);

    Optional<ExpanseCategory> findByNameIgnoreCaseAndUser_Id(String name, Long userId);

    Optional<Object> findByNameIgnoreCase(String name);
}