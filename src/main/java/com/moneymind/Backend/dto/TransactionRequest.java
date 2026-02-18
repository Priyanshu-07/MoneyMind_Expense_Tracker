package com.moneymind.Backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {
    @NotNull @Positive private BigDecimal amount;
    @NotBlank private String type;   // INCOME or EXPENSE
    private String description;
    private String tags;
    @NotNull private LocalDate transactionDate;
    private Long categoryId;
}