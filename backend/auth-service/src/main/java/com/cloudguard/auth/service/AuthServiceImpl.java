package com.cloudguard.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudguard.auth.dto.request.ForgotPasswordRequest;
import com.cloudguard.auth.dto.request.LoginRequest;
import com.cloudguard.auth.dto.request.RegisterRequest;
import com.cloudguard.auth.dto.request.ResetPasswordRequest;
import com.cloudguard.auth.dto.response.AuthResponse;
import com.cloudguard.auth.entity.PasswordResetToken;
import com.cloudguard.auth.entity.User;
import com.cloudguard.auth.exception.EmailAlreadyExistsException;
import com.cloudguard.auth.repository.PasswordResetTokenRepository;
import com.cloudguard.auth.repository.UserRepository;
import com.cloudguard.auth.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    // =========================
    // REGISTER
    // =========================

    @Override
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new EmailAlreadyExistsException(
                    "Email already registered: " + request.getEmail()
            );
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    // =========================
    // LOGIN
    // =========================

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return new AuthResponse(
                token,
                "Bearer",
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    // =========================
    // FORGOT PASSWORD
    // =========================

    @Override
    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Remove old reset tokens for this user
        passwordResetTokenRepository.deleteByUser(user);

        // Generate random token
        String token = UUID.randomUUID().toString();

        // Token valid for 15 minutes
        LocalDateTime expiryDate =
                LocalDateTime.now().plusMinutes(15);

        PasswordResetToken resetToken =
                PasswordResetToken.builder()
                        .token(token)
                        .user(user)
                        .expiryDate(expiryDate)
                        .used(false)
                        .build();

        passwordResetTokenRepository.save(resetToken);

        // Development only
        String resetLink =
                "http://localhost:5173/reset-password?token=" + token;

        System.out.println("----------------------------------------");
        System.out.println("PASSWORD RESET REQUEST");
        System.out.println("User: " + user.getEmail());
        System.out.println("Reset Link:");
        System.out.println(resetLink);
        System.out.println("Token expires at: " + expiryDate);
        System.out.println("----------------------------------------");

        return "Password reset link generated successfully";
    }

    // =========================
    // RESET PASSWORD
    // =========================

    @Override
    @Transactional
    public String resetPassword(ResetPasswordRequest request) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(request.getToken())
                        .orElseThrow(() ->
                                new RuntimeException("Invalid reset token")
                        );

        // Check whether token was already used
        if (resetToken.isUsed()) {

            throw new RuntimeException(
                    "Reset token has already been used"
            );
        }

        // Check token expiry
        if (resetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Reset token has expired"
            );
        }

        User user = resetToken.getUser();

        // Encrypt new password
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        // Invalidate token
        resetToken.setUsed(true);

        passwordResetTokenRepository.save(resetToken);

        return "Password reset successfully";
    }
}