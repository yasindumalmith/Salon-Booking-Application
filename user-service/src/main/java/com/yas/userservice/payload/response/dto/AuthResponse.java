package com.yas.userservice.payload.response.dto;

import com.yas.userservice.domain.UserRole;
import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private String refreshToken;
    private String message;
    private String title;
    private UserRole role;
}
