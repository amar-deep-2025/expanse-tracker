package com.amar.fullstack.expanse_tracker_backend.repository;

import com.amar.fullstack.expanse_tracker_backend.entity.ExpanseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpanseCategory,Long> {
}
