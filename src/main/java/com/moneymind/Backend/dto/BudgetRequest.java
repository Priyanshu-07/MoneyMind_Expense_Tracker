package com.moneymind.Backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BudgetRequest {
    @NotNull @Positive private BigDecimal limitAmount;
    @NotBlank private String month;       // "2024-01"
    private Integer alertThreshold;       // 80 = alert at 80%
    private Long categoryId;
}