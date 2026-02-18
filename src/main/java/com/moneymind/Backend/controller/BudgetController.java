package com.moneymind.Backend.controller;

import com.moneymind.Backend.dto.BudgetRequest;
import com.moneymind.Backend.entity.Budget;
import com.moneymind.Backend.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<Budget>> getAll(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(budgetService.getAllBudgets(user.getUsername()));
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<Budget>> getByMonth(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String month) {
        return ResponseEntity.ok(budgetService.getBudgetsByMonth(user.getUsername(), month));
    }

    @PostMapping
    public ResponseEntity<Budget> create(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.createBudget(user.getUsername(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Budget> update(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.updateBudget(user.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        budgetService.deleteBudget(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}