package com.estim.javaapi.presentation.profile;

public record PrivacySettingsResponse(
    boolean showProfile,
    boolean showActivity,
    boolean showWishlist
) {}
