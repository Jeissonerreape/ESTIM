package com.estim.javaapi.presentation.payment;

public record PaymentMethodResponse(
    String id,
    String provider,
    String last4,
    boolean isDefault
) {}
