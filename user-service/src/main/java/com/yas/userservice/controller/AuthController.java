package com.yas.userservice.controller;

import com.yas.userservice.payload.response.dto.AuthResponse;
import com.yas.userservice.payload.response.dto.LoginDTO;
import com.yas.userservice.payload.response.dto.SignupDTO;
import com.yas.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupDTO signupDTO) throws Exception {
        AuthResponse authResponse = authService.signup(signupDTO);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDTO loginDTO) throws Exception {
        AuthResponse authResponse=authService.login(loginDTO.getUsername(),loginDTO.getPassword());
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/access-token/refresh-token/{refreshToken}")
    public ResponseEntity<AuthResponse> refreshToken(@PathVariable String refreshToken) throws Exception {
        AuthResponse authResponse=authService.getAccessTokenFromFreshToken(refreshToken);
        return ResponseEntity.ok(authResponse);
    }

}
