package com.estim.javaapi.domain.user;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly-typed identifier for a user's payment method.
 */
public final class PaymentMethodId {

    private final UUID value;

    public PaymentMethodId(UUID value) {
        this.value = Objects.requireNonNull(value, "PaymentMethodId value cannot be null");
    }

    /**
     * Creates a PaymentMethodId from an existing UUID.
     */
    public static PaymentMethodId of(UUID value) {
        return new PaymentMethodId(value);
    }

    /**
     * Creates a PaymentMethodId from a string UUID.
     */
    public static PaymentMethodId fromString(String value) {
        return new PaymentMethodId(UUID.fromString(value));
    }

    /**
     * Generates a new random PaymentMethodId.
     */
    public static PaymentMethodId newId() {
        return new PaymentMethodId(UUID.randomUUID());
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
        if (!(obj instanceof PaymentMethodId other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
