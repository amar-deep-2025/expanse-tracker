package com.amar.fullstack.expanse_tracker_backend.repository;

import com.amar.fullstack.expanse_tracker_backend.entity.Budget;
import com.amar.fullstack.expanse_tracker_backend.entity.BudgetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget,Long> {

    List<Budget> findByUserIdOrderByYearDescMonthDesc(Long userId);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    Optional<Budget> findByUserIdAndMonthAndYearAndType(
            Long userId,
            Integer month,
            Integer year,
            BudgetType type
    );

    boolean existsByUserIdAndMonthAndYearAndTypeAndCategoryId(
            Long userId,
            Integer month,
            Integer year,
            BudgetType type,
            Long categoryId
    );
}
