package com.moneymind.Backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;        // e.g. "Food", "Rent", "Salary"

    @Column(nullable = false)
    private String type;        // "INCOME" or "EXPENSE"

    private String icon;        // optional emoji or icon name
    private String color;       // hex color for charts e.g. "#FF5733"

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;          // null = default/global category
}