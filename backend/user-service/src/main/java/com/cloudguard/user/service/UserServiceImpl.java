package com.cloudguard.user.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.cloudguard.user.dto.request.ChangePasswordRequest;
import com.cloudguard.user.dto.request.UpdateProfileRequest;
import com.cloudguard.user.dto.response.UserProfileResponse;
import com.cloudguard.user.entity.UserProfile;
import com.cloudguard.user.repository.UserProfileRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;

    private final RestClient restClient;


    public UserServiceImpl(
            UserProfileRepository userProfileRepository) {

        this.userProfileRepository = userProfileRepository;

        this.restClient =
                RestClient.create("http://localhost:8081");
    }


    // ==========================================
    // GET PROFILE
    // ==========================================

    @Override
    public UserProfileResponse getProfileByEmail(
            String email) {

        UserProfile userProfile =
                userProfileRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User profile not found"
                                )
                        );

        return convertToResponse(userProfile);
    }


    // ==========================================
    // UPDATE PROFILE
    // ==========================================

    @Override
    public UserProfileResponse updateProfileByEmail(
            String email,
            UpdateProfileRequest request) {

        UserProfile userProfile =
                userProfileRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User profile not found"
                                )
                        );

        userProfile.setName(request.getName());

        userProfile.setEmail(request.getEmail());

        UserProfile updatedProfile =
                userProfileRepository.save(userProfile);

        return convertToResponse(updatedProfile);
    }


    // ==========================================
    // CHANGE PASSWORD
    // ==========================================

    @Override
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        Map<String, String> body =
                new HashMap<>();

        body.put("email", email);

        body.put(
                "currentPassword",
                request.getCurrentPassword()
        );

        body.put(
                "newPassword",
                request.getNewPassword()
        );


        // Call Auth Service
        restClient
                .put()
                .uri("/api/auth/change-password")
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }


    // ==========================================
    // CONVERT ENTITY TO RESPONSE
    // ==========================================

    private UserProfileResponse convertToResponse(
            UserProfile userProfile) {

        return new UserProfileResponse(
                userProfile.getId(),
                userProfile.getUserId(),
                userProfile.getName(),
                userProfile.getEmail()
        );
    }
}