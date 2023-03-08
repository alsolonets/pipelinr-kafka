package com.example.pipelinrkafka.messagebus.kafka;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class KafkaRequestListenerTests {
    @Test
    void onMessage_whenMessageProcessor_expectProcessMessageAsync() {
        // given
        var messageProcessorCalled = new AtomicBoolean(false);
        var listener = new RequestListener((record, acknowledgment, consumer, responseSender) -> {
            messageProcessorCalled.set(true);
            return CompletableFuture.completedFuture(null);
        });

        // when
        listener.onMessage(null, null, null);

        // then
        assertTrue(messageProcessorCalled.get(), "Message processor should be called.");
    }
}