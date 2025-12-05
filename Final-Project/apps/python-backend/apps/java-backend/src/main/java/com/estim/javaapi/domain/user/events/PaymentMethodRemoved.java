package com.estim.javaapi.domain.user.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.user.PaymentMethodId;
import com.estim.javaapi.domain.user.UserId;

import java.time.Instant;

/**
 * Domain event raised when a payment method is removed from a user.
 */
public final class PaymentMethodRemoved implements DomainEvent {

    private final UserId userId;
    private final PaymentMethodId paymentMethodId;
    private final Instant occurredAt;

    public PaymentMethodRemoved(UserId userId, PaymentMethodId paymentMethodId, Instant occurredAt) {
        this.userId = userId;
        this.paymentMethodId = paymentMethodId;
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }

    public UserId userId() {
        return userId;
    }

    public PaymentMethodId paymentMethodId() {
        return paymentMethodId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
