package com.example.pipelinrkafka.messagebus.kafka;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.FailedDeserializationInfo;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class FailedDeserializationHandlerTests {

    public static final String TYPE_ID_HEADER_KEY = "__TypeId__";
    public static final String TYPE_ID_HEADER_VALUE = "TypeIdValue";
    public static final String TOPIC_NAME = "TOPIC";

    @Test
    void handleRequestDeserializationError_whenInfoInvalidType_expectIllegalArgumentException() {
        // given
        Object info = new Object();

        // when
        // then
        assertThrows(IllegalArgumentException.class, () ->
                FailedDeserializationHandler.handleRequestDeserializationError(info)
        );
    }

    @Test
    void handleResponseDeserializationError_whenInfoInvalidType_expectIllegalArgumentException() {
        // given
        Object info = new Object();

        // when
        // then
        assertThrows(IllegalArgumentException.class, () ->
                FailedDeserializationHandler.handleResponseDeserializationError(info)
        );
    }

    @Test
    void handleRequestDeserializationError_whenValidInfo_expectExceptionRequest() {
        // given
        FailedDeserializationInfo info = getFailedDeserializationInfo();

        // when
        var actual = FailedDeserializationHandler.handleRequestDeserializationError(info);

        // then
        validateException(actual.getException());
    }

    @Test
    void handleResponseDeserializationError_whenValidInfo_expectExceptionResponse() {
        // given
        FailedDeserializationInfo info = getFailedDeserializationInfo();

        // when
        var actual = FailedDeserializationHandler.handleResponseDeserializationError(info);

        // then
        validateException(actual.getException());
    }

    private FailedDeserializationInfo getFailedDeserializationInfo() {
        return new FailedDeserializationInfo(
                TOPIC_NAME,
                new RecordHeaders(
                        new Header[]{
                                new RecordHeader(
                                        TYPE_ID_HEADER_KEY,
                                        StandardCharsets.US_ASCII.encode(TYPE_ID_HEADER_VALUE).array()
                                )
                        }
                ),
                new byte[]{},
                false,
                null
        );
    }

    private void validateException(Throwable exception) {
        assertInstanceOf(ObjectDeserializationException.class, exception);
        var deserializationException = (ObjectDeserializationException) exception;
        assertEquals(TOPIC_NAME, deserializationException.getTopic(), "Exception should contain the correct topic.");
        assertEquals(TYPE_ID_HEADER_VALUE, deserializationException.getTypeName(), "Exception should contain the correct type.");
        assertTrue(deserializationException.getMessage().contains(TOPIC_NAME), "Exception message should contain the topic.");
        assertTrue(deserializationException.getMessage().contains(TYPE_ID_HEADER_VALUE), "Exception message should contain the type.");
    }
}
