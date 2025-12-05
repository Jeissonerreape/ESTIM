package com.estim.javaapi.domain.user;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Immutable value object representing a valid email address.
 */
public final class Email {

    // Very simple sanity-check regex; real validation is usually delegated to higher layers.
    private static final Pattern SIMPLE_EMAIL_REGEX =
        Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    public Email(String value) {
        this.value = value;
    }

    /**
     * Creates an Email from a raw string, applying basic validation and normalization.
     *
     * @throws IllegalArgumentException if the email is null, blank, or invalid.
     */
    public static Email of(String rawEmail) {
        if (rawEmail == null) {
            throw new IllegalArgumentException("email cannot be null");
        }

        String normalized = rawEmail.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("email cannot be empty");
        }

        // normalize to lower case (common for identity)
        normalized = normalized.toLowerCase();

        if (!SIMPLE_EMAIL_REGEX.matcher(normalized).matches()) {
            throw new IllegalArgumentException("invalid email format: " + rawEmail);
        }

        return new Email(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
