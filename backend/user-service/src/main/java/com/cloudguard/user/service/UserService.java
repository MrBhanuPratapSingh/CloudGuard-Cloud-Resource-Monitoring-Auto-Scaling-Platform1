package com.cloudguard.user.service;

import com.cloudguard.user.dto.request.ChangePasswordRequest;
import com.cloudguard.user.dto.request.UpdateProfileRequest;
import com.cloudguard.user.dto.response.UserProfileResponse;

public interface UserService {

    UserProfileResponse getProfileByEmail(String email);

    UserProfileResponse updateProfileByEmail(
            String email,
            UpdateProfileRequest request);

    void changePassword(
            String email,
            ChangePasswordRequest request);
}