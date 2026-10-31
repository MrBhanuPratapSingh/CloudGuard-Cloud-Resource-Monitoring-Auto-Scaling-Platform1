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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;

    private final RestClient restClient;
    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);


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
            ChangePasswordRequest request,
            String authorizationHeader) {

        Map<String, String> body = new HashMap<>();

        body.put("email", email);
        body.put("currentPassword", request.getCurrentPassword());
        body.put("newPassword", request.getNewPassword());

        try {

            restClient.put()
                    .uri("/api/auth/change-password")
                    .header("Authorization", authorizationHeader)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Password change request completed successfully");

        } catch (RestClientResponseException ex) {

            log.error(
                    "Auth Service returned status: {}, response: {}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
            );

            throw ex;

        } catch (ResourceAccessException ex) {

            log.error("Unable to connect to Auth Service", ex);

            throw ex;
        }
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