package com.estim.javaapi.presentation.password;

public record PasswordResetPerformRequest(
    String token,
    String newPassword
) {}
