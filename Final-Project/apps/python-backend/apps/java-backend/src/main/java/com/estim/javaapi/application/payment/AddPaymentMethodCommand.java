package com.estim.javaapi.application.payment;

/**
 * Input data for adding a payment method to a user.
 */
public record AddPaymentMethodCommand(
    String userId,        // internal user id (UUID string)
    String provider,      // e.g. "PAGSEGURO", "PAYPAL"
    String externalToken, // token/reference from payment provider vault
    String last4,         // last 4 digits for display
    boolean isDefault
) {}
