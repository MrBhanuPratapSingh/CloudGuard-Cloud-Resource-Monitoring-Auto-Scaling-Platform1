package com.cloudguard.auth.service;

import com.cloudguard.auth.dto.request.ForgotPasswordRequest;
import com.cloudguard.auth.dto.request.LoginRequest;
import com.cloudguard.auth.dto.request.RegisterRequest;
import com.cloudguard.auth.dto.request.ResetPasswordRequest;
import com.cloudguard.auth.dto.response.AuthResponse;

public interface AuthService {

    String register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    String forgotPassword(ForgotPasswordRequest request);

    String resetPassword(ResetPasswordRequest request);
}