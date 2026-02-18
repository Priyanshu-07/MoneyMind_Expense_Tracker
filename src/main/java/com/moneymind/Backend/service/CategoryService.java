package com.moneymind.Backend.service;

import com.moneymind.Backend.entity.Category;
import com.moneymind.Backend.entity.User;
import com.moneymind.Backend.repository.CategoryRepository;
import com.moneymind.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public List<Category> getAllCategories(String email) {
        User user = getUser(email);
        return categoryRepository.findByUserIdOrUserIsNull(user.getId());
    }

    public List<Category> getCategoriesByType(String email, String type) {
        User user = getUser(email);
        return categoryRepository.findByUserIdOrUserIsNullAndType(user.getId(), type);
    }

    public Category createCategory(String email, Category category) {
        User user = getUser(email);
        category.setUser(user);
        return categoryRepository.save(category);
    }

    public Category updateCategory(String email, Long id, Category categoryData) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.getUser() == null || !category.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        category.setName(categoryData.getName());
        category.setType(categoryData.getType());
        category.setIcon(categoryData.getIcon());
        category.setColor(categoryData.getColor());

        return categoryRepository.save(category);
    }

    public void deleteCategory(String email, Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.getUser() == null || !category.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        categoryRepository.delete(category);
    }
}