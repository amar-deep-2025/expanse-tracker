package com.amar.fullstack.expanse_tracker_backend.repository;

import com.amar.fullstack.expanse_tracker_backend.entity.Expanse;
import com.amar.fullstack.expanse_tracker_backend.entity.Type;
import com.amar.fullstack.expanse_tracker_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpanseRepository extends JpaRepository<Expanse, Long> {

        List<Expanse> findByUser_Id(Long userId);

        @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM Expanse e
    WHERE e.user.id = :userId
    AND e.type = 'EXPENSE'
""")
        Double getTotalExpense(Long userId);

        @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM Expanse e
    WHERE e.user.id = :userId
    AND e.type = 'INCOME'
""")
        Double getTotalIncome(Long userId);

        @Query("SELECT SUM(e.amount) FROM Expanse e WHERE e.user.id = :userId AND e.type = 'EXPENSE' AND e.expanseDate BETWEEN :startDate AND :endDate")
        Double getExpenseBetweenDates(Long userId,
                                      LocalDateTime startDate, LocalDateTime endDate);

        @Query("SELECT e.category.name, SUM(e.amount) FROM Expanse e WHERE e.user.id = :userId AND e.type = 'EXPENSE' AND e.expanseDate BETWEEN :startDate AND :endDate GROUP BY e.category.name")
        List<Object[]> getCategorySummary( Long userId,  LocalDateTime startDate,  LocalDateTime endDate);

        List<Expanse> findTop5ByUser_IdOrderByExpanseDateDesc( Long userId);

        @Query("SELECT SUM(e.amount) FROM Expanse e WHERE e.user.id = :userId AND e.type = 'INCOME' AND e.expanseDate BETWEEN :startDate AND :endDate")
        Double getIncomeBetweenDates(Long userId, LocalDateTime startDate, LocalDateTime endDate);

        @Query("""
                        SELECT
                        MONTH(e.expanseDate),
                        SUM(CASE WHEN e.type = 'INCOME' THEN e.amount ELSE 0 END),
                        SUM(CASE WHEN e.type = 'EXPENSE' THEN e.amount ELSE 0 END)
                        FROM Expanse e
                        WHERE e.user.id = :userId
                        AND YEAR(e.expanseDate) = :year
                        GROUP BY MONTH(e.expanseDate)
                        ORDER BY MONTH(e.expanseDate)
                        """)
        List<Object[]> getMonthlyIncomeExpense( Long userId,@Param("year") int year);

        @Query("""
                        SELECT SUM(e.amount) FROM Expanse e
                        WHERE e.user.id = :userId
                        AND e.type='EXPENSE'
                        AND MONTH(e.expanseDate)=:month
                        AND YEAR(e.expanseDate)=:year
                        """)
        Double getExpenseByMonth(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

        @Query("""
                        SELECT c.name, SUM(e.amount) FROM Expanse e
                        JOIN e.category c
                        WHERE e.user.id = :userId
                        AND e.type='EXPENSE'
                        GROUP BY c.name
                        ORDER BY SUM(e.amount) DESC
                        """)
        List<Object[]> getTopCategory(@Param("userId") Long userId);

        @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expanse e WHERE e.user.id = :userId AND e.type = :type")
        double getTotalByType(@Param("userId") Long userId,
                              @Param("type") Type type);
}