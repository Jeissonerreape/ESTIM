package com.estim.javaapi.presentation.profile;

public record UpdateUserProfileRequest(
    String displayName,
    String avatarUrl,
    String bio,
    String location,
    Boolean showProfile,
    Boolean showActivity,
    Boolean showWishlist
) {}

