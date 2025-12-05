package com.estim.javaapi.domain.user;

/**
 * Supported external payment providers for storing vaulted payment
 * methods or payment tokens. These do NOT contain raw card data.
 *
 * Each provider is responsible for securely storing the actual card
 * or payment method details and returning an external token to us.
 */
public enum PaymentProvider {

    /**
     * Brazilian payment platform. Common choice for local transactions.
     */
    PAGSEGURO,

    /**
     * PayPal vault tokens / billing agreements.
     */
    PAYPAL,

    /**
     * Stripe payment method tokens (PM_xxx) or customer payment sources.
     */
    STRIPE,

    /**
     * Placeholder for development or testing without a real provider.
     */
    MOCK;

    /**
     * Safely parse a provider string (case-insensitive).
     * Throws IllegalArgumentException for unsupported values.
     *
     * @param value provider identifier, e.g. "paypal", "PagSeguro"
     * @return the matching PaymentProvider enum
     */
    public static PaymentProvider fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payment provider cannot be empty");
        }

        String normalized = value.trim().toUpperCase();

        try {
            return PaymentProvider.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported payment provider: " + value);
        }
    }
}
