package com.example.pipelinrkafka.messagebus.kafka;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HeadersValueResolverTests {

    public static final String HEADER_NAME = "HEADER_NAME";
    public static final String OTHER_HEADER_NAME = "OTHER_HEADER_NAME";
    public static final String HEADER_VALUE = "HEADER_VALUE";
    public static final String TYPE_ID_HEADER_NAME = "__TypeId__";

    @Test
    void getHeaderAsString_whenHeadersNull_expectException() {
        // given
        Headers headers = null;

        // when/then
        assertThrows(IllegalArgumentException.class, () -> HeadersValueResolver.getHeaderAsString(headers, HEADER_NAME));
    }

    @Test
    void getHeaderAsString_whenHeaderNameNull_expectException() {
        // given
        Headers headers = new RecordHeaders();

        // when/then
        assertThrows(IllegalArgumentException.class, () -> HeadersValueResolver.getHeaderAsString(headers, null));
    }

    @Test
    void getHeaderAsString_whenHeadersEmpty_expectOptionalEmpty() {
        // given
        Headers headers = new RecordHeaders();

        // when
        Optional<String> actual = HeadersValueResolver.getHeaderAsString(headers, HEADER_NAME);

        // then
        assertTrue(actual.isEmpty(), "Expected empty result.");
    }

    @Test
    void getHeaderAsString_whenHeaderNotFound_expectOptionalEmpty() {
        // given
        byte[] value = new byte[] {};
        RecordHeader header = new RecordHeader(OTHER_HEADER_NAME, value);
        Headers headers = new RecordHeaders(new Header[] { header });

        // when
        Optional<String> actual = HeadersValueResolver.getHeaderAsString(headers, HEADER_NAME);

        // then
        assertTrue(actual.isEmpty(), "Expected empty result.");
    }

    @Test
    void getHeaderAsString_whenHeaderValueNull_expectOptionalEmpty() {
        // given
        byte[] value = null;
        RecordHeader header = new RecordHeader(HEADER_NAME, value);
        Headers headers = new RecordHeaders(new Header[] { header });

        // when
        Optional<String> actual = HeadersValueResolver.getHeaderAsString(headers, HEADER_NAME);

        // then
        assertTrue(actual.isEmpty(), "Expected empty result.");
    }

    @Test
    void getHeaderAsString_whenHeaderFound_expectHeaderValueAsString() {
        // given
        ByteBuffer value = StandardCharsets.US_ASCII.encode(HEADER_VALUE);
        RecordHeader header = new RecordHeader(HEADER_NAME, value.array());
        Headers headers = new RecordHeaders(new Header[] { header });

        // when
        Optional<String> actual = HeadersValueResolver.getHeaderAsString(headers, HEADER_NAME);

        // then
        assertTrue(actual.isPresent(), "Value should be present.");
        assertEquals(HEADER_VALUE, actual.get(), "Value should be equal to HEADER_VALUE.");
    }

    @Test
    void getTypeName_whenHeaderFound_expectTypeName() {
        // given
        ByteBuffer value = StandardCharsets.US_ASCII.encode(HEADER_VALUE);
        RecordHeader header = new RecordHeader(TYPE_ID_HEADER_NAME, value.array());
        Headers headers = new RecordHeaders(new Header[] { header });

        // when
        Optional<String> actual = HeadersValueResolver.getTypeName(headers);

        // then
        assertTrue(actual.isPresent(), "Value should be present.");
        assertEquals(HEADER_VALUE, actual.get(), "Value should be equal to HEADER_VALUE.");
    }
}

