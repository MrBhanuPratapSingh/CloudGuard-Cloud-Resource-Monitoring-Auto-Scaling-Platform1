package com.cloudguard.auth.service;

import com.cloudguard.auth.dto.request.ForgotPasswordRequest;
import com.cloudguard.auth.dto.request.LoginRequest;
import com.cloudguard.auth.dto.request.RegisterRequest;
import com.cloudguard.auth.dto.response.AuthResponse;

public interface AuthService {

    // Register
    String register(RegisterRequest request);

    // Login
    AuthResponse login(LoginRequest request);

    // Forgot Password
    String forgotPassword(ForgotPasswordRequest request);

    // Change Password
    void changePassword(
            String email,
            String currentPassword,
            String newPassword
    );
}