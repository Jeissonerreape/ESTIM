package com.estim.javaapi.presentation.auth;

public record CurrentUserResponse(
    String userId,
    String email,
    String displayName,
    String avatarUrl,
    boolean emailVerified
) {}
