package com.moneymind.Backend.repository;

import com.moneymind.Backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdOrUserIsNull(Long userId);
    List<Category> findByUserIdOrUserIsNullAndType(Long userId, String type);
}