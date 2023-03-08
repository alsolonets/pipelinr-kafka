package com.example.pipelinrkafka.messagebus.core;

import an.awesome.pipelinr.Command;

/**
 * The {@code ExceptionRequest} class represents a failed request due to deserialization errors or
 * other exceptions encountered while processing the request.
 * <p>
 * This class is intended to be used when handling errors during request processing, allowing the
 * error information to be included in the response sent back to the producer or other services.
 */
public class ExceptionRequest implements Command<ExceptionResponse> {
    /**
     * The exception containing the request processing error information.
     */
    private RuntimeException exception;

    public ExceptionRequest() {

    }

    public ExceptionRequest(RuntimeException exception) {
        this.exception = exception;
    }

    public RuntimeException getException() {
        return exception;
    }

    public void setException(RuntimeException exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "ExceptionRequest{" +
                "exception=" + exception +
                '}';
    }
}
