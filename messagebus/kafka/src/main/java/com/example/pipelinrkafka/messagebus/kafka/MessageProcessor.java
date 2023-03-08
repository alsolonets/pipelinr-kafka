package com.example.pipelinrkafka.messagebus.kafka;

import an.awesome.pipelinr.Command;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

import java.util.concurrent.CompletableFuture;

/**
 * The {@code MessageProcessor} interface defines a contract for processing messages received from Kafka.
 * Implementations of this interface should handle the deserialization, processing, and response
 * sending for each message consumed from a Kafka topic.
 * <p>
 * Typical usage involves implementing the {@code processMessageAsync} method, which takes a
 * {@code ConsumerRecord} containing the message, an {@code Acknowledgment} for committing
 * the offset, a {@code Consumer} for managing the consumer state, and a {@code ResponseSender}
 * for sending responses back to the producer or other services.
 * <p>
 * Implementations should handle messages asynchronously and return a {@code CompletableFuture<Void>}
 * that completes when the processing is done.
 */
@FunctionalInterface
public interface MessageProcessor {
    /**
     * Asynchronously processes a message received from Kafka.
     *
     * @param record         The {@link ConsumerRecord} containing the request.
     * @param acknowledgment The {@link Acknowledgment} for committing the message offset.
     * @param consumer       The {@link Consumer} for managing the Kafka consumer state.
     * @param responseSender The {@link ResponseSender} for sending responses back to the producer or other services.
     * @return A {@link CompletableFuture} that completes when the processing is done.
     */
    CompletableFuture<Void> processMessageAsync(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer, ResponseSender responseSender);
}
