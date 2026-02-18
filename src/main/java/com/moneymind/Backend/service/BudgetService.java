package com.moneymind.Backend.service;

import com.moneymind.Backend.dto.BudgetRequest;
import com.moneymind.Backend.entity.Budget;
import com.moneymind.Backend.entity.Category;
import com.moneymind.Backend.entity.User;
import com.moneymind.Backend.repository.BudgetRepository;
import com.moneymind.Backend.repository.CategoryRepository;
import com.moneymind.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public List<Budget> getAllBudgets(String email) {
        User user = getUser(email);
        return budgetRepository.findAll().stream()
                .filter(b -> b.getUser().getId().equals(user.getId()))
                .toList();
    }

    public List<Budget> getBudgetsByMonth(String email, String month) {
        User user = getUser(email);
        return budgetRepository.findByUserIdAndMonth(user.getId(), month);
    }

    public Budget createBudget(String email, BudgetRequest request) {
        User user = getUser(email);

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        }

        Budget budget = Budget.builder()
                .limitAmount(request.getLimitAmount())
                .month(request.getMonth())
                .alertThreshold(request.getAlertThreshold() != null ? request.getAlertThreshold() : 80)
                .user(user)
                .category(category)
                .build();

        return budgetRepository.save(budget);
    }

    public Budget updateBudget(String email, Long id, BudgetRequest request) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        }

        budget.setLimitAmount(request.getLimitAmount());
        budget.setMonth(request.getMonth());
        budget.setAlertThreshold(request.getAlertThreshold() != null ? request.getAlertThreshold() : 80);
        budget.setCategory(category);

        return budgetRepository.save(budget);
    }

    public void deleteBudget(String email, Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        if (!budget.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        budgetRepository.delete(budget);
    }
}