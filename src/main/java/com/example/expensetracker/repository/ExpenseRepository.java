package com.example.expensetracker.repository;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCategory(String category);
    List<Expense> findByAmountBetween(Double min, Double max);
    List<Expense> findByCategoryAndAmountBetween(String category, Double min, Double max);
    List<Expense> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String title,
            String category
    );
    @Query("SELECT COALESCE(SUM(e.amount),0) FROM Expense e")
    Double getTotalAmount();
    @Query("""
       SELECT e.category, SUM(e.amount)
       FROM Expense e
       GROUP BY e.category
       """)
    List<Object[]> getTotalAmountByCategory();
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.createdDate BETWEEN :start AND :end")
    Double getTotalByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM Expense e
    WHERE e.createdDate >= :start
    AND e.createdDate < :end
""")
    Double getTotalByMonth(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM Expense e
    WHERE e.createdDate >= :start
    AND e.createdDate < :end
""")
    Double getTotalByYear(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    @Query("SELECT COALESCE(AVG(e.amount), 0) FROM Expense e")
    Double getAverageAmount();
    Optional<Expense> findTopByOrderByAmountDesc();
    Optional<Expense> findTopByOrderByAmountAsc();
    long count();
    long countByCategory(String category);
    @Query("""
    SELECT e.category, COUNT(e)
    FROM Expense e
    GROUP BY e.category
""")
    List<Object[]> countExpensesByCategory();
    @Query("""
    SELECT MONTH(e.createdDate), SUM(e.amount)
    FROM Expense e
    WHERE YEAR(e.createdDate) = :year
    GROUP BY MONTH(e.createdDate)
    ORDER BY MONTH(e.createdDate)
""")
    List<Object[]> getMonthlySummary(@Param("year") int year);
    List<Expense> findByUser(User user);

    Optional<Expense> findByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);

    @Modifying
    @Query("DELETE FROM Expense e WHERE e.id = :id AND e.user = :user")
    void deleteByIdAndUser(
            @Param("id") Long id,
            @Param("user") User user
    );
    Page<Expense> findByUserId(Long userId, Pageable pageable);}
