package com.estim.javaapi.domain.common;

import java.time.Instant;

/**
 * Marker + metadata contract for all domain events.
 */
public interface DomainEvent {

    /**
     * The moment when the event occurred.
     */
    Instant occurredAt();
}
