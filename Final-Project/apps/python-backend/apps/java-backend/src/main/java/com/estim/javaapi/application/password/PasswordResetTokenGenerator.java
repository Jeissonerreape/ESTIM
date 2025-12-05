package com.estim.javaapi.application.password;

/**
 * Abstraction for generating password reset tokens.
 * Implementations can use secure random strings, UUIDs, etc.
 */
public interface PasswordResetTokenGenerator {

    String generate();
}
