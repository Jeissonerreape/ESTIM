package com.estim.javaapi.infrastructure.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.common.DomainEventPublisher;

import java.util.List;
import java.util.Objects;

/**
 * DomainEventPublisher implementation using the Outbox pattern.
 *
 * Instead of sending events directly to a message broker, it serializes
 * them and stores them in an outbox table. A separate process or job
 * later reads the outbox and publishes to external systems (Kafka, etc.).
 */
public class OutboxEventPublisher implements DomainEventPublisher {

    private final OutboxRepository outboxRepository;
    private final EventSerializer serializer;

    public OutboxEventPublisher(OutboxRepository outboxRepository,
                                EventSerializer serializer) {

        this.outboxRepository = Objects.requireNonNull(outboxRepository);
        this.serializer = Objects.requireNonNull(serializer);
    }

    @Override
    public void publish(DomainEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        ExternalEventEnvelope envelope = serializer.serialize(event);
        OutboxEvent outboxEvent = OutboxEvent.fromEnvelope(envelope);

        outboxRepository.save(outboxEvent);
    }

    @Override
    public void publishAll(List<? extends DomainEvent> events) {
        for (DomainEvent event : events) {
            publish(event);
        }
    }
}
