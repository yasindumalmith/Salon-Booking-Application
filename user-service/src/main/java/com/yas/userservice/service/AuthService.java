package com.yas.userservice.service;

import com.yas.userservice.payload.response.dto.AuthResponse;
import com.yas.userservice.payload.response.dto.SignupDTO;

public interface AuthService {
    AuthResponse login(String username, String password) throws Exception;
    AuthResponse signup(SignupDTO signupDTO) throws Exception;
    AuthResponse getAccessTokenFromFreshToken(String refreshToken) throws Exception;
}
