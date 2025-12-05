package com.estim.javaapi.application.auth;

/**
 * Input data for logging out a user.
 *
 * Typically the client sends the refresh token to be revoked.
 * Access token is optional (you may want to parse it to know who is logging out).
 */
public record LogoutUserCommand(
    String accessToken,
    String refreshToken
) {}
