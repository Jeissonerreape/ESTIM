package com.estim.javaapi.domain.user;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly-typed identifier for User aggregates.
 */
public final class UserId {

    private final UUID value;

    public UserId(UUID value) {
        this.value = Objects.requireNonNull(value, "UserId value cannot be null");
    }

    /**
     * Creates a UserId from an existing UUID.
     */
    public static UserId of(UUID value) {
        return new UserId(value);
    }

    /**
     * Creates a UserId from a string UUID.
     */
    public static UserId fromString(String value) {
        return new UserId(UUID.fromString(value));
    }

    /**
     * Generates a new random UserId.
     */
    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UserId other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
