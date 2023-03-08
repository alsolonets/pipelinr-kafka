package com.example.pipelinrkafka.messagebus.kafka.requestreply;

import org.springframework.kafka.requestreply.ReplyingKafkaOperations;
import org.springframework.kafka.requestreply.RequestReplyFuture;

/**
 * Operations implementing the "reply-to" pattern in Kafka.
 * See <a href="https://github.com/callistaenterprise/blog-synchronous-kafka">blog-synchronous-kafka</a>.
 *
 * @param <K> the type of the key.
 * @param <V> the type of the request.
 * @param <R> the type of the response.
 */
public interface PartitionAwareReplyingKafkaOperations<K, V, R> extends ReplyingKafkaOperations<K, V, R> {

    /**
     * Sends a request to the default topic.
     *
     * @param value the request.
     * @return {@link RequestReplyFuture} with the response.
     */
    RequestReplyFuture<K, V, R> sendAndReceiveDefault(V value);

    /**
     * Sends a request to the default topic.
     *
     * @param key   the key.
     * @param value the request.
     * @return {@link RequestReplyFuture} with the response.
     */
    RequestReplyFuture<K, V, R> sendAndReceiveDefault(K key, V value);

    /**
     * Sends a request to the default topic.
     *
     * @param partition the partition to send the request to.
     * @param key       the key.
     * @param value     the request.
     * @return {@link RequestReplyFuture} with the response.
     */
    RequestReplyFuture<K, V, R> sendAndReceiveDefault(Integer partition, K key, V value);

    /**
     * Sends a request to the default topic.
     *
     * @param partition the partition to send the request to.
     * @param timestamp the creation time of the record in milliseconds since epoch.
     * @param key       the key.
     * @param value     the request.
     * @return {@link RequestReplyFuture} with the response.
     */
    RequestReplyFuture<K, V, R> sendAndReceiveDefault(Integer partition, Long timestamp, K key, V value);

    /**
     * Sends a request to the specified topic.
     *
     * @param topic the topic to send the request to.
     * @param value the request.
     * @return {@link RequestReplyFuture} with the response.
     */
    RequestReplyFuture<K, V, R> sendAndReceive(String topic, V value);

    /**
     * Sends a request to the specified topic.
     *
     * @param topic the topic to send the request to.
     * @param key   the key.
     * @param value the request.
     * @return {@link RequestReplyFuture} with the response.
     */
    RequestReplyFuture<K, V, R> sendAndReceive(String topic, K key, V value);

    /**
     * Sends a request to the specified topic.
     *
     * @param topic     the topic to send the request to.
     * @param partition the partition to send the request to.
     * @param key       the key.
     * @param value     the request.
     * @return {@link RequestReplyFuture} with the response.
     */
    RequestReplyFuture<K, V, R> sendAndReceive(String topic, Integer partition, K key, V value);

    /**
     * Sends a request to the specified topic.
     *
     * @param topic     the topic to send the request to.
     * @param partition the partition to send the request to.
     * @param timestamp the creation time of the record in milliseconds since epoch.
     * @param key       the key.
     * @param value     the request.
     * @return {@link RequestReplyFuture} with the response.
     */
    RequestReplyFuture<K, V, R> sendAndReceive(String topic, Integer partition, Long timestamp, K key, V value);
}
