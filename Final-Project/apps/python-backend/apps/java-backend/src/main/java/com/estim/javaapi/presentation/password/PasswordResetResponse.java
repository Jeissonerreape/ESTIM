package com.estim.javaapi.presentation.password;

public record PasswordResetResponse(
    boolean success,
    String message
) {}
