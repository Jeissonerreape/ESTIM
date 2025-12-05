package com.estim.javaapi.application.payment;

/**
 * Input data for removing a payment method from a user.
 */
public record RemovePaymentMethodCommand(
    String userId,          // internal user id (UUID string)
    String paymentMethodId  // internal payment method id (UUID string)
) {}
