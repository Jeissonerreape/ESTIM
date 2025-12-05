package com.estim.javaapi.infrastructure.events;

import com.estim.javaapi.domain.common.DomainEvent;
import com.estim.javaapi.domain.common.DomainEventPublisher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Objects;

/**
 * DomainEventPublisher implementation that publishes events to a Kafka topic.
 *
 * Requires dependency:
 *   implementation "org.apache.kafka:kafka-clients:3.7.0" (or similar)
 */
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final EventSerializer serializer;

    public KafkaDomainEventPublisher(KafkaProducer<String, String> producer,
                                     String topic,
                                     EventSerializer serializer) {

        this.producer = Objects.requireNonNull(producer);
        this.topic = Objects.requireNonNull(topic);
        this.serializer = Objects.requireNonNull(serializer);
    }

    @Override
    public void publish(DomainEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        ExternalEventEnvelope envelope = serializer.serialize(event);

        // Use event type as key so same type partitions together (or change strategy)
        ProducerRecord<String, String> record =
            new ProducerRecord<>(topic, envelope.type(), envelope.payload());

        // Fire-and-forget send; you might want to handle callbacks or futures in real code.
        producer.send(record);
    }

    @Override
    public void publishAll(List<? extends DomainEvent> events) {
        DomainEventPublisher.super.publishAll(events);
    }
}
