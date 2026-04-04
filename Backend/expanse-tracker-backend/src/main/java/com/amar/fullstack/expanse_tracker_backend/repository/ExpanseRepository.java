package com.amar.fullstack.expanse_tracker_backend.repository;
import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpanseRepository extends JpaRepository<Expanse, Long> {

    List<Expanse> findByUserId(Long userId);

    @Query("SELECT SUM(e.amount) FROM Expanse e WHERE e.user = :user AND e.type = 'EXPENSE'")
    Double getTotalExpense(User user);

    @Query("SELECT SUM(e.amount) FROM Expanse e WHERE e.user = :user AND e.type = 'INCOME'")
    Double getTotalIncome(User user);

    @Query("SELECT SUM(e.amount) FROM Expanse e WHERE e.user = :user AND e.type = 'EXPENSE' AND e.expanseDate BETWEEN :startDate AND :endDate")
    Double getExpenseBetweenDates(User user, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT e.category.name, SUM(e.amount) FROM Expanse e WHERE e.user = :user AND e.type = 'EXPENSE' AND e.expanseDate BETWEEN :startDate AND :endDate GROUP BY e.category.name")
    List<Object[]> getCategorySummary(User user, LocalDateTime startDate, LocalDateTime endDate);

    List<Expanse> findTop5ByUserOrderByExpanseDateDesc(User user);

    @Query("SELECT SUM(e.amount) FROM Expanse e WHERE e.user = :user AND e.type = INCOME AND e.expanseDate BETWEEN :startDate AND :endDate")
    Double getIncomeBetweenDates(User user, LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
          SELECT 
          MONTH(e.expanseDate),
          SUM(CASE WHEN e.type = 'INCOME' THEN e.amount ELSE 0 END),
          SUM(CASE WHEN e.type = 'EXPENSE' THEN e.amount ELSE 0 END)
          FROM Expanse e
          WHERE e.user = :user 
          AND YEAR(e.expanseDate) = :year
          GROUP BY MONTH(e.expanseDate)
          ORDER BY MONTH(e.expanseDate)""")
          List<Object[]> getMonthlyIncomeExpense(User user, int year);
}