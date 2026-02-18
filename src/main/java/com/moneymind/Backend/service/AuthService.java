//package com.moneymind.Backend.service;
//
//import com.moneymind.Backend.config.JwtUtil;
//import com.moneymind.Backend.dto.*;
//import com.moneymind.Backend.entity.User;
//import com.moneymind.Backend.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.*;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
//    private final AuthenticationManager authenticationManager;
//
//    public AuthResponse register(RegisterRequest request) {
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email already in use");
//        }
//        var user = User.builder()
//                .name(request.getName())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .build();
//        userRepository.save(user);
//        String token = jwtUtil.generateToken(user.getEmail());
//        return new AuthResponse(token, user.getName(), user.getEmail());
//    }
//
//    public AuthResponse login(LoginRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
//        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
//        String token = jwtUtil.generateToken(user.getEmail());
//        return new AuthResponse(token, user.getName(), user.getEmail());
//    }
//}

package com.moneymind.Backend.service;

import com.moneymind.Backend.config.JwtUtil;
import com.moneymind.Backend.dto.*;
import com.moneymind.Backend.entity.Category;
import com.moneymind.Backend.entity.User;
import com.moneymind.Backend.repository.CategoryRepository;
import com.moneymind.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        user = userRepository.save(user);

        // Create default categories for the new user
        createDefaultCategories(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getName(), user.getEmail());
    }

    private void createDefaultCategories(User user) {
        List<Category> defaultCategories = Arrays.asList(
                // Income Categories
                Category.builder().name("Salary").type("INCOME").icon("💰").color("#10b981").user(user).build(),
                Category.builder().name("Freelance").type("INCOME").icon("💼").color("#3b82f6").user(user).build(),
                Category.builder().name("Investment").type("INCOME").icon("📈").color("#8b5cf6").user(user).build(),
                Category.builder().name("Gift").type("INCOME").icon("🎁").color("#ec4899").user(user).build(),

                // Expense Categories
                Category.builder().name("Food & Dining").type("EXPENSE").icon("🍔").color("#ef4444").user(user).build(),
                Category.builder().name("Transportation").type("EXPENSE").icon("🚗").color("#f59e0b").user(user).build(),
                Category.builder().name("Shopping").type("EXPENSE").icon("🛍️").color("#ec4899").user(user).build(),
                Category.builder().name("Entertainment").type("EXPENSE").icon("🎬").color("#8b5cf6").user(user).build(),
                Category.builder().name("Healthcare").type("EXPENSE").icon("🏥").color("#14b8a6").user(user).build(),
                Category.builder().name("Bills & Utilities").type("EXPENSE").icon("💡").color("#f97316").user(user).build(),
                Category.builder().name("Education").type("EXPENSE").icon("📚").color("#3b82f6").user(user).build(),
                Category.builder().name("Other").type("EXPENSE").icon("📦").color("#6b7280").user(user).build()
        );

        categoryRepository.saveAll(defaultCategories);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getName(), user.getEmail());
    }
}