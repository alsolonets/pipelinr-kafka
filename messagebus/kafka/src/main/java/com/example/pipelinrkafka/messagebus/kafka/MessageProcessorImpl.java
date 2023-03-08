package com.example.pipelinrkafka.messagebus.kafka;

import com.example.pipelinrkafka.messagebus.core.ExceptionRequest;
import com.example.pipelinrkafka.messagebus.core.ExceptionResponse;
import com.example.pipelinrkafka.messagebus.kafka.configuration.KafkaRequestConsumerConfiguration;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Multithreaded Kafka message handler.
 */
public class MessageProcessorImpl implements MessageProcessor {
    protected final Logger logger = LoggerFactory.getLogger(MessageProcessorImpl.class);
    private final Function<Object, Object> responseProvider;
    private final Duration replyTimeout;
    private final Semaphore semaphore;
    public final Executor threadPool;

    /**
     * How much time does the message processor idles if it reaches its capacity
     * until next attempt to process an incoming message. This value (1 second)
     * is for demonstration purposes only. The value cannot be less than poll timeout
     * (see {@code KafkaRequestConsumerConfiguration.pollTimeout}).
     */
    private Duration nackSleepDuration = Duration.ofSeconds(1);

    public MessageProcessorImpl(Function<Object, Object> responseProvider, Duration replyTimeout, Integer maxConcurrentThreadsCount) {
        this.responseProvider = responseProvider;
        this.replyTimeout = replyTimeout;
        this.threadPool = Executors.newFixedThreadPool(maxConcurrentThreadsCount);
        this.semaphore = new Semaphore(maxConcurrentThreadsCount);
    }

    public MessageProcessorImpl(Function<Object, Object> responseProvider, Duration replyTimeout, Integer maxConcurrentThreadsCount, Duration nackSleepDuration) {
        this(responseProvider, replyTimeout, maxConcurrentThreadsCount);
        this.nackSleepDuration = nackSleepDuration;
    }

    @Override
    public CompletableFuture<Void> processMessageAsync(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer, ResponseSender responseSender) {
        if (!semaphore.tryAcquire()) {
            logThrottling();
            acknowledgment.nack(nackSleepDuration);
            return CompletableFuture.completedFuture(null);
        }

        acknowledgment.acknowledge();
        logMessageReceived(record);

        return CompletableFuture
                .completedFuture(record.value())
                .thenApplyAsync(this::getResponse, threadPool)
                .orTimeout(replyTimeout.toMillis(), TimeUnit.MILLISECONDS)
                .handle(this::handleResponseOrException)
                .thenAccept(response -> responseSender.sendResponse(record, acknowledgment, consumer, response))
                .whenComplete((unused, throwable) -> semaphore.release());
    }

    private Object getResponse(Object request) {
        if (request instanceof ExceptionRequest exceptionRequest) {
            return new ExceptionResponse(exceptionRequest.getException());
        } else {
            return responseProvider.apply(request);
        }
    }

    private Object handleResponseOrException(@Nullable Object response, @Nullable Throwable throwable) {
        if (response == null) {
            if (throwable == null) {
                throwable = new RuntimeException("Received null instead of a response to the request.");
            } else if (throwable instanceof CompletionException && throwable.getCause() != null) {
                throwable = throwable.getCause();
            }
            response = new ExceptionResponse(throwable);
        }

        if (response instanceof ExceptionResponse) {
            throwable = ((ExceptionResponse) response).getException();
        }

        if (throwable != null) {
            logger.error("An error occurred while processing the request.", throwable);
        }

        return response;
    }

    private void logMessageReceived(ConsumerRecord<String, Object> record) {
        if (logger.isTraceEnabled()) {
            logger.trace(
                    "Received request '{}'. Partition {}. Offset {}.",
                    HeadersValueResolver
                            .getTypeName(record.headers())
                            .orElse(HeadersValueResolver.UNKNOWN_TYPE_NAME),
                    record.partition(),
                    record.offset()
            );
        }
    }

    private void logThrottling() {
        if (logger.isWarnEnabled()) {
            logger.warn("Kafka message processor reached its capacity. Throttling.");
        }
    }
}
