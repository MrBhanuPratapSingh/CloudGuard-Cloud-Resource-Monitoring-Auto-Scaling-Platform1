package com.cloudguard.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.cloudguard.user.dto.request.ChangePasswordRequest;
import com.cloudguard.user.dto.request.UpdateProfileRequest;
import com.cloudguard.user.dto.response.UserProfileResponse;
import com.cloudguard.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    // ==========================================
    // GET PROFILE
    // ==========================================

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            Authentication authentication) {

        String email = authentication.getName();

        UserProfileResponse response =
                userService.getProfileByEmail(email);

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // UPDATE PROFILE
    // ==========================================

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        String email = authentication.getName();

        UserProfileResponse response =
                userService.updateProfileByEmail(
                        email,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // CHANGE PASSWORD
    // ==========================================

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request) {

        // Email comes from JWT
        String email = authentication.getName();

        userService.changePassword(
                email,
                request
        );

        return ResponseEntity.ok(
                "Password changed successfully"
        );
    }
}