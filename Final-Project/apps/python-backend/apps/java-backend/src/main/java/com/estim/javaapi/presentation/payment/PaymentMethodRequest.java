package com.estim.javaapi.presentation.payment;

public record PaymentMethodRequest(
    String provider,   // e.g. "PAGSEGURO", "PAYPAL"
    String token,      // vaulted token / external reference, NOT raw card
    String last4,      // last 4 digits for display
    boolean isDefault
) {}
