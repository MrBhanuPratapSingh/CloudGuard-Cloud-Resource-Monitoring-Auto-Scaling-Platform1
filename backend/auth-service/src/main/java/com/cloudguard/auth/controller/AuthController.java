package com.cloudguard.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cloudguard.auth.dto.request.ForgotPasswordRequest;
import com.cloudguard.auth.dto.request.LoginRequest;
import com.cloudguard.auth.dto.request.RegisterRequest;
import com.cloudguard.auth.dto.request.ResetPasswordRequest;
import com.cloudguard.auth.dto.response.AuthResponse;
import com.cloudguard.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    // REGISTER

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        String response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    // login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
    // FORGOT PASSWORD
    

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        String response =
                authService.forgotPassword(request);

        return ResponseEntity.ok(response);
    }

    
    // RESET PASSWORD
   

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        String response =
                authService.resetPassword(request);

        return ResponseEntity.ok(response);
    }
}