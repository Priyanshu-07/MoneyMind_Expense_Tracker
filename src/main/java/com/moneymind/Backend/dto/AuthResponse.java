package com.moneymind.Backend.dto;

import lombok.*;

@Data @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String name;
    private String email;
}