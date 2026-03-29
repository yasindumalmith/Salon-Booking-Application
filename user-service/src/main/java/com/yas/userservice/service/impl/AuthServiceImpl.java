package com.yas.userservice.service.impl;

import com.yas.userservice.model.User;
import com.yas.userservice.payload.response.dto.AuthResponse;
import com.yas.userservice.payload.response.dto.SignupDTO;
import com.yas.userservice.payload.response.dto.TokenResponse;
import com.yas.userservice.repository.UserRepository;
import com.yas.userservice.service.AuthService;
import com.yas.userservice.service.KeyClockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final KeyClockService keyClockService;
    @Override
    public AuthResponse login(String username, String password) throws Exception {
        TokenResponse tokenResponse = keyClockService.getAdminAccessToken(username,password,
                "password",null);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setRefreshToken(tokenResponse.getRefreshToken());
        authResponse.setJwt(tokenResponse.getAccessToken());
        authResponse.setMessage("Login Success");

        return authResponse;
    }

    @Override
    public AuthResponse signup(SignupDTO signupDTO) throws Exception {
        keyClockService.createUser(signupDTO);

        User newUser = new User();
        newUser.setUsername(signupDTO.getUsername());
        newUser.setPassword(signupDTO.getPassword());
        newUser.setEmail(signupDTO.getEmail());
        newUser.setRole(signupDTO.getRole());
        newUser.setFullname(signupDTO.getFirstName() + " " + signupDTO.getLastName());
        newUser.setCreatedAt(LocalDateTime.now());

        userRepository.save(newUser);

        TokenResponse tokenResponse = keyClockService.getAdminAccessToken(signupDTO.getUsername(),signupDTO.getPassword(),
                "password",null);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setRefreshToken(tokenResponse.getRefreshToken());
        authResponse.setJwt(tokenResponse.getAccessToken());
        authResponse.setRole(newUser.getRole());
        authResponse.setMessage("Register Success");

        return authResponse;
    }

    @Override
    public AuthResponse getAccessTokenFromFreshToken(String refreshToken) throws Exception {
        TokenResponse tokenResponse = keyClockService.getAdminAccessToken(null,null,
                "refresh_token",refreshToken);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setRefreshToken(tokenResponse.getRefreshToken());
        authResponse.setJwt(tokenResponse.getAccessToken());
        authResponse.setMessage("Access Token Refresh Success");

        return authResponse;
    }
}
