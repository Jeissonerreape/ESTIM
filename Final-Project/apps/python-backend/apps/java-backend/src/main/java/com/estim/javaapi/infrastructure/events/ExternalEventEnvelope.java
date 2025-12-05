package com.estim.javaapi.infrastructure.events;

import java.time.Instant;

/**
 * Generic envelope for events leaving the domain boundary.
 */
public record ExternalEventEnvelope(
    String type,       // e.g. fully-qualified event class name or logical type
    String payload,    // typically JSON
    Instant occurredAt // from DomainEvent.occurredAt()
) {}
