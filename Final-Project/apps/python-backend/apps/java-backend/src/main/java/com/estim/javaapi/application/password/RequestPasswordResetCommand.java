package com.estim.javaapi.application.password;

/**
 * Input data for requesting a password reset.
 */
public record RequestPasswordResetCommand(
    String email
) {}
