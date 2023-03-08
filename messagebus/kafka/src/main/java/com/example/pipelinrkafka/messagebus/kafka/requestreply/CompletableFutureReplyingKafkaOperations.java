package com.example.pipelinrkafka.messagebus.kafka.requestreply;

import java.util.concurrent.CompletableFuture;

/**
 * Operations implementing the request-reply pattern in Kafka using {@link CompletableFuture}.
 * See <a href="https://github.com/callistaenterprise/blog-synchronous-kafka">blog-synchronous-kafka</a>.
 *
 * @param <K> the type of the key.
 * @param <V> the type of the request.
 * @param <R> the type of the response.
 */
public interface CompletableFutureReplyingKafkaOperations<K, V, R> {

    /**
     * Sends a request to the default topic.
     *
     * @param value the request.
     * @return {@link CompletableFuture} with the response.
     */
    CompletableFuture<R> requestReplyDefault(V value);

    /**
     * Sends a request to the default topic.
     *
     * @param key   the key.
     * @param value the request.
     * @return {@link CompletableFuture} with the response.
     */
    CompletableFuture<R> requestReplyDefault(K key, V value);

    /**
     * Sends a request to the default topic.
     *
     * @param partition the partition to send the request to.
     * @param key       the key.
     * @param value     the request.
     * @return {@link CompletableFuture} with the response.
     */
    CompletableFuture<R> requestReplyDefault(Integer partition, K key, V value);

    /**
     * Sends a request to the default topic.
     *
     * @param partition the partition to send the request to.
     * @param timestamp the creation time of the record in milliseconds since epoch.
     * @param key       the key.
     * @param value     the request.
     * @return {@link CompletableFuture} with the response.
     */
    CompletableFuture<R> requestReplyDefault(Integer partition, Long timestamp, K key, V value);

    /**
     * Sends a request to the specified topic.
     *
     * @param topic the topic to send the request to.
     * @param value the request.
     * @return {@link CompletableFuture} with the response.
     */
    CompletableFuture<R> requestReply(String topic, V value);

    /**
     * Sends a request to the specified topic.
     *
     * @param topic the topic to send the request to.
     * @param key   the key.
     * @param value the request.
     * @return {@link CompletableFuture} with the response.
     */
    CompletableFuture<R> requestReply(String topic, K key, V value);

    /**
     * Sends a request to the specified topic.
     *
     * @param topic     the topic to send the request to.
     * @param partition the partition to send the request to.
     * @param key       the key.
     * @param value     the request.
     * @return {@link CompletableFuture} with the response.
     */
    CompletableFuture<R> requestReply(String topic, Integer partition, K key, V value);

    /**
     * Sends a request to the specified topic.
     *
     * @param topic     the topic to send the request to.
     * @param partition the partition to send the request to.
     * @param timestamp the creation time of the record in milliseconds since epoch.
     * @param key       the key.
     * @param value     the request.
     * @return {@link CompletableFuture} with the response.
     */
    CompletableFuture<R> requestReply(String topic, Integer partition, Long timestamp, K key, V value);

}
