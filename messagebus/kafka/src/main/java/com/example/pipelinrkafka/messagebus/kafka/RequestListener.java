package com.example.pipelinrkafka.messagebus.kafka;

import an.awesome.pipelinr.Command;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.adapter.RecordMessagingMessageListenerAdapter;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;

/**
 * Listens requests on the request topic in Kafka,
 * processes them using the {@link MessageProcessor} and sends the response back to the response topic.
 */
public class RequestListener extends RecordMessagingMessageListenerAdapter<String, Object> {
    protected final Logger logger = LoggerFactory.getLogger(RequestListener.class);
    private final String EMPTY_STRING = "";
    private final MessageProcessor messageProcessor;

    /**
     * Creates a new instance of {@link RequestListener} class.
     *
     * @param messageProcessor the Kafka message processor.
     */
    public RequestListener(@NonNull MessageProcessor messageProcessor) {
        super(null, null);
        this.setReplyTopic(EMPTY_STRING); // иначе не работает отправка ответа.
        this.messageProcessor = messageProcessor;
    }

    /**
     * An unused constructor.
     */
    private RequestListener(Object bean, Method method) {
        super(bean, method);
        messageProcessor = null;
    }

    /**
     * An unused constructor.
     */
    private RequestListener(Object bean, Method method, KafkaListenerErrorHandler errorHandler) {
        super(bean, method, errorHandler);
        messageProcessor = null;
    }

    /**
     * This method is being called when a request appears in the request topic.
     *
     * @param record         The consumer record containing the request.
     * @param acknowledgment The acknowledgment for committing the message offset.
     * @param consumer       The consumer for managing the Kafka consumer state.
     */
    @Override
    public void onMessage(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        this.messageProcessor.processMessageAsync(record, acknowledgment, consumer, this::sendResponse);
    }

    /**
     * Sends the response back to the request sender.
     *
     * @param record         The consumer record containing the request.
     * @param acknowledgment The acknowledgment for committing the message offset.
     * @param consumer       The consumer for managing the Kafka consumer state.
     * @param response       The response to the request.
     */
    protected void sendResponse(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer, Object response) {
        try {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Sending the response {}.", response.getClass().getSimpleName());
            }
            var message = toMessagingMessage(record, acknowledgment, consumer);
            handleResult(response, record, message);
        } catch (Throwable ex) {
            this.logger.error("Unable to send the response.", ex);
        }
    }
}
