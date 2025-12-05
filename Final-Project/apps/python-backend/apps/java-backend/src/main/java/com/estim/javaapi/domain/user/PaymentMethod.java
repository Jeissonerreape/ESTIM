package com.estim.javaapi.domain.user;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a vaulted payment method stored for a user.
 * Only stores provider, masked reference, and external vault token.
 */
public class PaymentMethod {

    private final PaymentMethodId id;
    private final PaymentProvider provider;
    private final String externalToken; // token from payment gateway (not card)
    private final String last4;         // last 4 digits for display only
    private final boolean isDefault;

    public PaymentMethod(PaymentMethodId id,
                         PaymentProvider provider,
                         String externalToken,
                         String last4,
                         boolean isDefault) {

        this.id = Objects.requireNonNull(id);
        this.provider = Objects.requireNonNull(provider);
        this.externalToken = Objects.requireNonNull(externalToken);
        this.last4 = Objects.requireNonNull(last4);
        this.isDefault = isDefault;
    }

    public PaymentMethodId id() {
        return id;
    }

    public PaymentProvider provider() {
        return provider;
    }

    public String externalToken() {
        return externalToken;
    }

    public String last4() {
        return last4;
    }

    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Returns a new PaymentMethod with updated default flag.
     * (Domain entities remain effectively immutable.)
     */
    public PaymentMethod markDefault(boolean value) {
        return new PaymentMethod(this.id, this.provider, this.externalToken, this.last4, value);
    }

    public static PaymentMethod newMethod(PaymentProvider provider,
                                          String externalToken,
                                          String last4,
                                          boolean isDefault) {
        return new PaymentMethod(new PaymentMethodId(UUID.randomUUID()), provider, externalToken, last4, isDefault);
    }
}
