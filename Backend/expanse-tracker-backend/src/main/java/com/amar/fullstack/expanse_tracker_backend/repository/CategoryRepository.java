package com.amar.fullstack.expanse_tracker_backend.repository;
import com.amar.fullstack.expanse_tracker_backend.entity.ExpanseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<ExpanseCategory,Long> {
    Optional<ExpanseCategory> findByNameIgnoreCase(String name);
}
