package com.moneymind.Backend.service;

import com.moneymind.Backend.dto.DashboardResponse;
import com.moneymind.Backend.entity.*;
import com.moneymind.Backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public DashboardResponse getDashboard(String email, String month) {
        User user = userRepository.findByEmail(email).orElseThrow();

        // Parse month (e.g. "2024-01")
        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        BigDecimal totalIncome = transactionRepository
                .sumByUserAndTypeAndDateRange(user.getId(), "INCOME", start, end);
        BigDecimal totalExpense = transactionRepository
                .sumByUserAndTypeAndDateRange(user.getId(), "EXPENSE", start, end);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        // Expense by category (for pie chart)
        List<Transaction> transactions = transactionRepository
                .findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                        user.getId(), start, end);

        Map<String, BigDecimal> categoryMap = new LinkedHashMap<>();
        for (Transaction tx : transactions) {
            if ("EXPENSE".equals(tx.getType())) {
                String catName = tx.getCategory() != null ? tx.getCategory().getName() : "Other";
                categoryMap.merge(catName, tx.getAmount(), BigDecimal::add);
            }
        }
        List<Map<String, Object>> expenseByCategory = new ArrayList<>();
        categoryMap.forEach((name, amount) -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", name);
            entry.put("value", amount);
            expenseByCategory.add(entry);
        });

        // Monthly trend (last 6 months)
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth m = ym.minusMonths(i);
            LocalDate s = m.atDay(1);
            LocalDate e = m.atEndOfMonth();
            BigDecimal inc = transactionRepository.sumByUserAndTypeAndDateRange(user.getId(), "INCOME", s, e);
            BigDecimal exp = transactionRepository.sumByUserAndTypeAndDateRange(user.getId(), "EXPENSE", s, e);
            Map<String, Object> entry = new HashMap<>();
            entry.put("month", m.format(DateTimeFormatter.ofPattern("MMM")));
            entry.put("income", inc);
            entry.put("expense", exp);
            trend.add(entry);
        }

        // Budget alerts
        List<Budget> budgets = budgetRepository.findByUserIdAndMonth(user.getId(), month);
        List<DashboardResponse.BudgetAlertDto> alerts = new ArrayList<>();
        for (Budget budget : budgets) {
            BigDecimal spent = budget.getCategory() != null
                    ? transactionRepository.sumExpensesByUserAndCategory(
                    user.getId(), budget.getCategory().getId(), start, end)
                    : totalExpense;

            int pct = budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0
                    ? spent.multiply(BigDecimal.valueOf(100))
                    .divide(budget.getLimitAmount(), 0, java.math.RoundingMode.HALF_UP)
                    .intValue()
                    : 0;

            int threshold = budget.getAlertThreshold() != null ? budget.getAlertThreshold() : 80;

            if (pct >= threshold) {
                String catName = budget.getCategory() != null
                        ? budget.getCategory().getName() : "Overall";
                alerts.add(new DashboardResponse.BudgetAlertDto(
                        catName, budget.getLimitAmount(), spent, pct, spent.compareTo(budget.getLimitAmount()) >= 0));
            }
        }

        return new DashboardResponse(totalIncome, totalExpense, balance,
                expenseByCategory, trend, alerts);
    }
}