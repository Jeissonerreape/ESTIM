package com.estim.javaapi.application.payment;

/**
 * Abstraction for validating payment method tokens with an external provider.
 *
 * Implementations can call PagSeguro, PayPal, Stripe, etc. to ensure
 * the provided token is valid and usable.
 */
public interface PaymentProviderClient {

    /**
     * Validates the provided token for the given provider.
     *
     * Implementations should throw an exception if the token is invalid, expired,
     * or does not belong to the user.
     */
    void validateToken(String provider, String externalToken);
}
