package com.moneymind.Backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @AllArgsConstructor @NoArgsConstructor
public class DashboardResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private List<Map<String, Object>> expenseByCategory;
    private List<Map<String, Object>> monthlyTrend;
    private List<BudgetAlertDto> budgetAlerts;

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class BudgetAlertDto {
        private String categoryName;
        private BigDecimal budgetLimit;
        private BigDecimal spent;
        private int percentage;
        private boolean exceeded;
    }
}