package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.user.User;

/**
 * Result of a successful authentication.
 * Application layer returns this and the presentation layer maps it to DTOs.
 */
public record AuthenticationResult(
    String accessToken,
    String refreshToken,
    User user
) {}
