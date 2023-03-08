package com.example.pipelinrkafka.messagebus.kafka;

/**
 * Represents a deserialization exception of an object from kafka.
 */
public class ObjectDeserializationException extends RuntimeException {
    /**
     * The topic name.
     */
    private String topic;

    /**
     * The type name.
     */
    private String typeName;

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public ObjectDeserializationException() {
    }

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public ObjectDeserializationException(String topic, String typeName) {
        this.topic = topic;
        this.typeName = typeName;
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ObjectDeserializationException(String message, String topic, String typeName) {
        super(message);
        this.topic = topic;
        this.typeName = typeName;
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public ObjectDeserializationException(String message, Throwable cause, String topic, String typeName) {
        super(message, cause);
        this.topic = topic;
        this.typeName = typeName;
    }

    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of
     * {@code cause}).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public ObjectDeserializationException(Throwable cause, String topic, String typeName) {
        super(cause);
        this.topic = topic;
        this.typeName = typeName;
    }

    /**
     * Constructs a new runtime exception with the specified detail
     * message, cause, suppression enabled or disabled, and writable
     * stack trace enabled or disabled.
     *
     * @param message            the detail message.
     * @param cause              the cause.  (A {@code null} value is permitted,
     *                           and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression  whether or not suppression is enabled
     *                           or disabled
     * @param writableStackTrace whether or not the stack trace should
     *                           be writable
     * @since 1.7
     */
    public ObjectDeserializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String topic, String typeName) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.topic = topic;
        this.typeName = typeName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "KafkaObjectDeserializationException{" +
                "topic='" + topic + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
