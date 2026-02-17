package com.moneymind.Backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal limitAmount;     // monthly budget limit

    @Column(nullable = false)
    private String month;               // format: "2024-01"

    @Column(name = "alert_threshold")
    private Integer alertThreshold;     // e.g. 80 means alert at 80%

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;          // null = overall budget
}