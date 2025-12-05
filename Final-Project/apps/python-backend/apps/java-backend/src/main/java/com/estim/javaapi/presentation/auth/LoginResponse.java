package com.estim.javaapi.presentation.auth;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    AuthenticatedUserSummary user
) {}
