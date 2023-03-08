package com.example.pipelinrkafka.messagebus.kafka;

import an.awesome.pipelinr.Command;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

/**
 * Функция отправки результата в kafka.
 */
@FunctionalInterface
public interface ResponseSender {

    /**
     * Выполняет отправку результата в kafka.
     *
     * @param record         The consumer record containing the request.
     * @param acknowledgment The acknowledgment for committing the message offset.
     * @param consumer       The consumer for managing the Kafka consumer state.
     * @param response       The response to the request.
     */
    void sendResponse(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer, Object response);
}
