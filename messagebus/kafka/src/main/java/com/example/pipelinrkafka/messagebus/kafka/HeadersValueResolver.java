package com.example.pipelinrkafka.messagebus.kafka;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.kafka.support.mapping.AbstractJavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME;

/**
 * Gets the value of a Kafka record's header.
 */
public class HeadersValueResolver {
    public static final String UNKNOWN_TYPE_NAME = "Unknown type";

    /**
     * Gets the header value as a string.
     *
     * @param headers    headers collection
     * @param headerName the header name
     * @return the header value as string
     */
    public static Optional<String> getHeaderAsString(Headers headers, String headerName) {
        if (headers == null) {
            throw new IllegalArgumentException("headers");
        }
        if (headerName == null) {
            throw new IllegalArgumentException("headerName");
        }
        Header header = headers.lastHeader(headerName);
        if (header != null) {
            if (header.value() != null) {
                String headerValue = new String(header.value(), StandardCharsets.UTF_8);
                return Optional.of(headerValue);
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the type name.
     *
     * @param headers the headers collection
     * @return the type name
     */
    public static Optional<String> getTypeName(Headers headers) {
        return getHeaderAsString(headers, DEFAULT_CLASSID_FIELD_NAME);
    }
}
