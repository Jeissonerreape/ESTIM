package com.estim.javaapi.application.password;

/**
 * Input data for performing a password reset using a reset token.
 */
public record ResetPasswordCommand(
    String token,
    String newPassword
) {}
