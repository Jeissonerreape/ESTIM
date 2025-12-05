package com.estim.javaapi.presentation.auth;

public record AuthenticatedUserSummary(
    String userId,
    String email,
    String displayName,
    boolean emailVerified
) {}
