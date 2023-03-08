package com.example.pipelinrkafka.messagebus.core;

/**
 * The {@code ExceptionResponse} class represents a failed response due to deserialization errors or
 * other exceptions encountered while processing the response.
 * <p>
 * This class is intended to be used when handling errors during response processing, allowing the
 * error information to be included in the response sent back to the producer or other services.
 */
public class ExceptionResponse {
    /**
     * The exception containing the response processing error information.
     */
    private Throwable exception;

    public ExceptionResponse() {
    }

    public ExceptionResponse(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "ExceptionResponse{" +
                "exception=" + exception +
                '}';
    }
}
