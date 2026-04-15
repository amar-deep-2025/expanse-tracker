package com.amar.fullstack.expanse_tracker_backend.repository;

import com.amar.fullstack.expanse_tracker_backend.entity.Budget;
import com.amar.fullstack.expanse_tracker_backend.entity.BudgetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query("SELECT COALESCE(SUM(b.budget), 0) FROM Budget b WHERE b.user.id = :userId")
    Double getTotalBudget(Long userId);

    @Query("""
       SELECT SUM(b.budget) FROM Budget b
    WHERE b.user.id = :userId
    AND b.month = :month
    AND b.year = :year
""")
    Double getTotalBudgetByMonth(Long userId, Integer month, Integer year);

    @Query("""
         SELECT COALESCE(SUM(b.budget), 0) FROM Budget b
        WHERE b.user.id = :userId
        AND (
        (b.year > :startYear OR (b.year = :startYear AND b.month >= :startMonth))
        AND
        (b.year < :endYear OR (b.year = :endYear AND b.month <= :endMonth))
        )
     """)
    Double getBudgetBetweenMonths(
            Long userId,
            Integer startMonth,
            Integer startYear,
            Integer endMonth,
            Integer endYear
    );



}
