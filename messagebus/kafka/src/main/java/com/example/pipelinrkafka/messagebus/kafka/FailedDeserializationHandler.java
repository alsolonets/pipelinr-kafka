package com.example.pipelinrkafka.messagebus.kafka;

import com.example.pipelinrkafka.messagebus.core.ExceptionRequest;
import com.example.pipelinrkafka.messagebus.core.ExceptionResponse;
import org.springframework.kafka.support.serializer.FailedDeserializationInfo;
import org.springframework.lang.NonNull;

/**
 * Handles deserialization errors from kafka.
 */
public class FailedDeserializationHandler {

    public static final String INFO_ARGUMENT_TYPE_MESSAGE = "info must be of type FailedDeserializationInfo.";
    public static final String EXCEPTION_MESSAGE_FORMAT = "Failed to deserialize '%s' from topic '%s'. Check that the default constructor and setters exist and are accessible.";

    /**
     * Handles a request deserialization error from kafka.
     *
     * @param info An object of type {@link FailedDeserializationInfo} with the deserialization error information.
     * @return A new request having the deserialization error information.
     */
    @NonNull
    public static ExceptionRequest handleRequestDeserializationError(Object info) {
        ObjectDeserializationException exception = getKafkaObjectDeserializationException(info);
        return new ExceptionRequest(exception);
    }

    /**
     * Handles a response deserialization error from kafka.
     *
     * @param info An object of type {@link FailedDeserializationInfo} with the deserialization error information.
     * @return A new response having the deserialization error information.
     */
    @NonNull
    public static ExceptionResponse handleResponseDeserializationError(Object info) {
        ObjectDeserializationException exception = getKafkaObjectDeserializationException(info);
        return new ExceptionResponse(exception);
    }

    @NonNull
    private static ObjectDeserializationException getKafkaObjectDeserializationException(Object info) {
        if (info instanceof FailedDeserializationInfo failedDeserializationInfo) {
            String typeName = extractTypeName(failedDeserializationInfo);
            return createObjectDeserializationException(typeName, failedDeserializationInfo.getTopic(), failedDeserializationInfo.getException());
        } else {
            throw new IllegalArgumentException(INFO_ARGUMENT_TYPE_MESSAGE);
        }
    }

    private static String extractTypeName(FailedDeserializationInfo failedDeserializationInfo) {
        return HeadersValueResolver
                .getTypeName(failedDeserializationInfo.getHeaders())
                .orElse(HeadersValueResolver.UNKNOWN_TYPE_NAME);
    }

    private static ObjectDeserializationException createObjectDeserializationException(String typeName, String topic, Throwable cause) {
        String exceptionMessage = String.format(EXCEPTION_MESSAGE_FORMAT, typeName, topic);
        return new ObjectDeserializationException(exceptionMessage, cause, topic, typeName);
    }
}
