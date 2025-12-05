package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.user.PasswordHash;

/**
 * Abstraction for hashing and verifying passwords.
 * Implemented using BCrypt, Argon2, etc. in the infrastructure layer.
 */
public interface PasswordHasher {

    PasswordHash hash(String rawPassword);

    boolean matches(String rawPassword, PasswordHash hash);
}
