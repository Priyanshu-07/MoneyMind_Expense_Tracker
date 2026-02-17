package com.moneymind.Backend.repository;

import com.moneymind.Backend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);

    List<Transaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = :type " +
            "AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal sumByUserAndTypeAndDateRange(Long userId, String type,
                                            LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = 'EXPENSE' " +
            "AND t.category.id = :categoryId " +
            "AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal sumExpensesByUserAndCategory(Long userId, Long categoryId,
                                            LocalDate start, LocalDate end);
}