package com.example.pipelinrkafka.messagebus.kafka;

import com.example.pipelinrkafka.messagebus.core.ExceptionRequest;
import com.example.pipelinrkafka.messagebus.core.ExceptionResponse;
import com.example.pipelinrkafka.messagebus.kafka.infrastructure.AcknowledgmentMock;
import com.example.pipelinrkafka.messagebus.kafka.infrastructure.TestRequest;
import com.example.pipelinrkafka.messagebus.kafka.infrastructure.TestResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class MessageProcessorImplTests {
    public static final String TOPIC = "";
    public static final int PARTITION = 0;
    public static final int OFFSET = 0;
    public static final OffsetResetStrategy OFFSET_RESET_STRATEGY = OffsetResetStrategy.NONE;
    public static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(1);
    public static final int ONE_CONCURRENT_THREAD = 1;
    public static final int MANY_CONCURRENT_THREADS = 100;
    public static final Duration SLOW_RESPONSE_DURATION = Duration.ofSeconds(1);
    public static final Duration QUICK_RESPONSE_DURATION = Duration.ofMillis(100);
    public static final int MANY_CONCURRENT_THREADS_TIMEOUT_SEC = 1;
    public static final String REQUEST_HANDLER_EXCEPTION_MESSAGE = "Request handler exception.";

    @Test
    void processMessageAsync_whenRequest_expectSendResponse() throws ExecutionException, InterruptedException {
        // given
        var request = new TestRequest();
        var expectedResponse = new TestResponse();
        var record = new ConsumerRecord<String, Object>(TOPIC, PARTITION, OFFSET, null, request);
        var acknowledgment = new AcknowledgmentMock();
        var consumer = new MockConsumer<>(OFFSET_RESET_STRATEGY);
        Function<Object, Object> responseProvider = (unused) -> expectedResponse;
        var messageProcessor = new MessageProcessorImpl(responseProvider, DEFAULT_TIMEOUT, ONE_CONCURRENT_THREAD);
        var actualResponseReference = new AtomicReference<>();

        // when
        messageProcessor.processMessageAsync(record, acknowledgment, consumer, (__, ___, ____, actualResponse) -> {
            actualResponseReference.set(actualResponse);
        }).get();

        var actualResponse = actualResponseReference.get();

        // then
        assertSame(expectedResponse, actualResponse, "Response should be from the response provider");
        assertEquals(1, acknowledgment.getAckCount(), "Request receipt should be acknowledged once");
        assertEquals(0, acknowledgment.getNackCount(), "There should be no rejection of request receipt");
    }

    @Test
    void processMessageAsync_whenRequestHandlerException_expectExceptionResponse() throws ExecutionException, InterruptedException {
        // given
        var request = new TestRequest();
        var record = new ConsumerRecord<String, Object>(TOPIC, PARTITION, OFFSET, null, request);
        var acknowledgment = new AcknowledgmentMock();
        var consumer = new MockConsumer<>(OFFSET_RESET_STRATEGY);
        var requestHandlerException = new RuntimeException(REQUEST_HANDLER_EXCEPTION_MESSAGE);
        Function<Object, Object> responseProvider = (unused) -> {
            throw requestHandlerException;
        };
        var messageProcessor = new MessageProcessorImpl(responseProvider, DEFAULT_TIMEOUT, ONE_CONCURRENT_THREAD);
        var actualResponseReference = new AtomicReference<>();

        // when
        messageProcessor.processMessageAsync(record, acknowledgment, consumer, (__, ___, ____, actualResponse) -> {
            actualResponseReference.set(actualResponse);
        }).get();

        var actualResponse = actualResponseReference.get();

        // then
        assertInstanceOf(ExceptionResponse.class, actualResponse, "Response should be with an error");
        assertSame(requestHandlerException, ((ExceptionResponse) actualResponse).getException(), "The error should be the one thrown in the handler");
    }

    @Test
    void processMessageAsync_whenTimeout_expectExceptionResponse() throws ExecutionException, InterruptedException {
        // given
        var request = new TestRequest();
        var record = new ConsumerRecord<String, Object>(TOPIC, PARTITION, OFFSET, null, request);
        var acknowledgment = new AcknowledgmentMock();
        var consumer = new MockConsumer<>(OFFSET_RESET_STRATEGY);
        var timeout = Duration.ZERO;
        var responseProvider = getDelayedResponseFunction(SLOW_RESPONSE_DURATION);
        var messageProcessor = new MessageProcessorImpl(responseProvider, timeout, ONE_CONCURRENT_THREAD);
        var actualResponseReference = new AtomicReference<>();

        // when
        messageProcessor.processMessageAsync(record, acknowledgment, consumer, (__, ___, ____, actualResponse) -> {
            actualResponseReference.set(actualResponse);
        }).get();

        var actualResponse = actualResponseReference.get();

        // then
        assertInstanceOf(ExceptionResponse.class, actualResponse, "Response should be with an error");
        assertInstanceOf(TimeoutException.class, ((ExceptionResponse) actualResponse).getException(), "The error should be TimeoutException");
    }

    @Test
    void processMessageAsync_whenNullResponse_expectExceptionResponse() throws ExecutionException, InterruptedException {
        // given
        var request = new TestRequest();
        var record = new ConsumerRecord<String, Object>(TOPIC, PARTITION, OFFSET, null, request);
        var acknowledgment = new AcknowledgmentMock();
        var consumer = new MockConsumer<>(OFFSET_RESET_STRATEGY);
        Function<Object, Object> responseProvider = (unused) -> null;
        var messageProcessor = new MessageProcessorImpl(responseProvider, DEFAULT_TIMEOUT, ONE_CONCURRENT_THREAD);
        var actualResponseReference = new AtomicReference<>();

        // when
        messageProcessor.processMessageAsync(record, acknowledgment, consumer, (__, ___, ____, actualResponse) -> {
            actualResponseReference.set(actualResponse);
        }).get();

        var actualResponse = actualResponseReference.get();

        // then
        assertInstanceOf(ExceptionResponse.class, actualResponse, "Response should be with an error");
        assertInstanceOf(RuntimeException.class, ((ExceptionResponse) actualResponse).getException(), "The error should be RuntimeException");
    }

    @Test
    void processMessageAsync_whenExceptionRequest_expectExceptionResponse() throws ExecutionException, InterruptedException {
        // given
        var exception = new RuntimeException();
        var request = new ExceptionRequest(exception);
        var record = new ConsumerRecord<String, Object>(TOPIC, PARTITION, OFFSET, null, request);
        var acknowledgment = new AcknowledgmentMock();
        var consumer = new MockConsumer<>(OFFSET_RESET_STRATEGY);
        Function<Object, Object> responseProvider = (unused) -> {
            throw new IllegalStateException("This code should not be executed.");
        };
        var messageProcessor = new MessageProcessorImpl(responseProvider, DEFAULT_TIMEOUT, ONE_CONCURRENT_THREAD);
        var actualResponseReference = new AtomicReference<>();

        // when
        messageProcessor.processMessageAsync(record, acknowledgment, consumer, (__, ___, ____, actualResponse) -> {
            actualResponseReference.set(actualResponse);
        }).get();

        var actualResponse = actualResponseReference.get();

        // then
        assertInstanceOf(ExceptionResponse.class, actualResponse, "Response should be with an error");
        assertSame(exception, ((ExceptionResponse) actualResponse).getException(), "The error in the response should be taken from the request");
    }

    @Test
    void processMessageAsync_whenThreadLimitExceeded_expectNack() throws ExecutionException, InterruptedException {
        // given
        var request = new TestRequest();
        var expectedResponse = new TestResponse();
        var record1 = new ConsumerRecord<String, Object>(TOPIC, PARTITION, OFFSET, null, request);
        var record2 = new ConsumerRecord<String, Object>(TOPIC, PARTITION, OFFSET, null, request);
        var acknowledgment1 = new AcknowledgmentMock();
        var acknowledgment2 = new AcknowledgmentMock();
        var consumer = new MockConsumer<>(OFFSET_RESET_STRATEGY);
        var responseProviderCallCount = new AtomicInteger();
        Function<Object, Object> responseProvider = (unused) -> {
            sleep(QUICK_RESPONSE_DURATION);
            responseProviderCallCount.incrementAndGet();
            return expectedResponse;
        };
        var messageProcessor = new MessageProcessorImpl(responseProvider, DEFAULT_TIMEOUT, ONE_CONCURRENT_THREAD);

        var future1 = messageProcessor.processMessageAsync(record1, acknowledgment1, consumer, getEmptyResponseSender());
        var future2 = messageProcessor.processMessageAsync(record2, acknowledgment2, consumer, getEmptyResponseSender());

        // when
        CompletableFuture.allOf(future1, future2).get();

        // then
        assertEquals(1, acknowledgment1.getAckCount(), "The first request reception should be acknowledged");
        assertEquals(1, acknowledgment2.getNackCount(), "The second request reception should be rejected");
        assertEquals(1, responseProviderCallCount.get(), "Request should be processed once");
    }

    @Test
    void processMessageAsync_whenBusyThreadsComplete_expectAcknowledgeAndProcess() throws ExecutionException, InterruptedException {
        // given
        var request = new TestRequest();
        var expectedResponse = new TestResponse();
        var record1 = new ConsumerRecord<String, Object>(TOPIC, PARTITION, OFFSET, null, request);
        var record2 = new ConsumerRecord<String, Object>(TOPIC, PARTITION, OFFSET, null, request);
        var acknowledgment1 = new AcknowledgmentMock();
        var acknowledgment2 = new AcknowledgmentMock();
        var consumer = new MockConsumer<>(OFFSET_RESET_STRATEGY);
        var responseProviderCallCount = new AtomicInteger();
        Function<Object, Object> responseProvider = (unused) -> {
            sleep(QUICK_RESPONSE_DURATION);
            responseProviderCallCount.incrementAndGet();
            return expectedResponse;
        };
        var messageProcessor = new MessageProcessorImpl(responseProvider, DEFAULT_TIMEOUT, ONE_CONCURRENT_THREAD);

        // when
        var future1 = messageProcessor.processMessageAsync(record1, acknowledgment1, consumer, getEmptyResponseSender()).get();
        var future2 = messageProcessor.processMessageAsync(record2, acknowledgment2, consumer, getEmptyResponseSender()).get();

        // then
        assertEquals(1, acknowledgment1.getAckCount(), "The first request reception should be acknowledged");
        assertEquals(1, acknowledgment2.getAckCount(), "The second request reception should be acknowledged");
        assertEquals(2, responseProviderCallCount.get(), "Both requests should be processed");
    }

    @Test
    @Timeout(value = MANY_CONCURRENT_THREADS_TIMEOUT_SEC)
    void processMessageAsync_whenManyConcurrentThreads_expectRunParallel() throws ExecutionException, InterruptedException {
        // given
        var request = new TestRequest();
        var expectedResponse = new TestResponse();
        var record = new ConsumerRecord<String, Object>(TOPIC, PARTITION, OFFSET, null, request);
        var acknowledgment = new AcknowledgmentMock();
        var consumer = new MockConsumer<>(OFFSET_RESET_STRATEGY);
        Function<Object, Object> responseProvider = (unused) -> {
            sleep(QUICK_RESPONSE_DURATION);
            return expectedResponse;
        };
        var messageProcessor = new MessageProcessorImpl(responseProvider, DEFAULT_TIMEOUT, MANY_CONCURRENT_THREADS);

        var futures = new CompletableFuture[MANY_CONCURRENT_THREADS];
        for (var i = 0; i < MANY_CONCURRENT_THREADS; i++) {
            var future = messageProcessor.processMessageAsync(record, acknowledgment, consumer, getEmptyResponseSender());
            futures[i] = future;
        }

        // when
        CompletableFuture.allOf(futures).get();

        // then
        assertEquals(MANY_CONCURRENT_THREADS, acknowledgment.getAckCount(), "All request receptions should be acknowledged");
        assertEquals(0, acknowledgment.getNackCount(), "No request should be rejected");
    }

    @NonNull
    Function<Object, Object> getDelayedResponseFunction(Duration executionTime) {
        return (unused) -> {
            sleep(executionTime);
            return new TestResponse();
        };
    }

    ResponseSender getEmptyResponseSender() {
        return (record, acknowledgment, consumer, response) -> {
        };
    }

    void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
