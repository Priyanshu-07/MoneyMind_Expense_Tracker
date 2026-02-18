package com.moneymind.Backend.controller;

import com.moneymind.Backend.entity.Category;
import com.moneymind.Backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAll(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(categoryService.getAllCategories(user.getUsername()));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Category>> getByType(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String type) {
        return ResponseEntity.ok(categoryService.getCategoriesByType(user.getUsername(), type));
    }

    @PostMapping
    public ResponseEntity<Category> create(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(user.getUsername(), category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(user.getUsername(), id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        categoryService.deleteCategory(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}