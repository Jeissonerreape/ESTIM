package com.estim.javaapi.presentation.auth;

public record RegisterUserResponse(
    String userId,
    String email,
    String displayName,
    boolean emailVerified
) {}

