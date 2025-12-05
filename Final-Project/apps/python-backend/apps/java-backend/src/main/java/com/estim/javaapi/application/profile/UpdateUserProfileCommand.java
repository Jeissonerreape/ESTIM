package com.estim.javaapi.application.profile;

/**
 * Input data for updating the profile of the currently authenticated user.
 *
 * All fields are nullable to support partial updates:
 * if a field is null, the existing value is preserved.
 */
public record UpdateUserProfileCommand(
    String userId,
    String displayName,
    String avatarUrl,
    String bio,
    String location,
    Boolean showProfile,
    Boolean showActivity,
    Boolean showWishlist
) {}
