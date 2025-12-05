package com.estim.javaapi.infrastructure.payment;

import com.estim.javaapi.application.payment.PaymentProviderClient;
import org.springframework.stereotype.Component;

/**
 * No-op implementation of PaymentProviderClient that accepts all tokens.
 * Useful for development/testing before integrating with a real payment gateway.
 */
@Component
public class NoopPaymentProviderClient implements PaymentProviderClient {

    @Override
    public void validateToken(String provider, String externalToken) {
        // Intentionally do nothing: always treat token as valid.
        // Replace with real validation against PagSeguro/Stripe/etc. later.
    }
}
