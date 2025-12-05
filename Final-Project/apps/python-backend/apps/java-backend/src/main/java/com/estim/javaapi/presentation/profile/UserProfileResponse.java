package com.estim.javaapi.presentation.profile;

public record UserProfileResponse(
    String userId,
    String displayName,
    String avatarUrl,
    String bio,
    String location,
    PrivacySettingsResponse privacy
) {}
