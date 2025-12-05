package com.estim.javaapi.domain.user;

import java.util.Objects;

/**
 * Immutable value object representing a hashed password.
 *
 * IMPORTANT: this class assumes the value is already hashed.
 * Hashing and verification should be done by a dedicated service
 * in the application/infrastructure layer (e.g., BCryptPasswordHasher).
 */
public final class PasswordHash {

    private final String value;

    public PasswordHash(String value) {
        this.value = value;
    }

    /**
     * Creates a PasswordHash from an already hashed password string.
     *
     * @throws IllegalArgumentException if the hash is null or empty.
     */
    public static PasswordHash of(String hashedPassword) {
        if (hashedPassword == null) {
            throw new IllegalArgumentException("password hash cannot be null");
        }
        String normalized = hashedPassword.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("password hash cannot be empty");
        }
        return new PasswordHash(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        // DO NOT include the real hash in logs in a real system.
        // Here we just return it; be careful when logging this.
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordHash other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
