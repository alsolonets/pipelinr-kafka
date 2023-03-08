package com.example.pipelinrkafka.messagebus.kafka.requestreply;

import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.GenericMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import java.util.concurrent.CompletableFuture;

/**
 * A variation of {@link ReplyingKafkaTemplate} that adapts {@link RequestReplyFuture} to {@link CompletableFuture}
 * as the return type.
 * See <a href="https://github.com/callistaenterprise/blog-synchronous-kafka">blog-synchronous-kafka</a>.
 */
public class CompletableFutureReplyingKafkaTemplate<K, V, R> extends PartitionAwareReplyingKafkaTemplate<K, V, R>
        implements CompletableFutureReplyingKafkaOperations<K, V, R> {

    public CompletableFutureReplyingKafkaTemplate(ProducerFactory<K, V> producerFactory,
                                                  GenericMessageListenerContainer<K, R> replyContainer) {
        super(producerFactory, replyContainer);
    }

    @Override
    public CompletableFuture<R> requestReplyDefault(V value) {
        return adapt(sendAndReceiveDefault(value));
    }

    @Override
    public CompletableFuture<R> requestReplyDefault(K key, V value) {
        return adapt(sendAndReceiveDefault(key, value));
    }

    @Override
    public CompletableFuture<R> requestReplyDefault(Integer partition, K key, V value) {
        return adapt(sendAndReceiveDefault(partition, key, value));
    }

    @Override
    public CompletableFuture<R> requestReplyDefault(Integer partition, Long timestamp, K key, V value) {
        return adapt(sendAndReceiveDefault(partition, timestamp, key, value));
    }

    @Override
    public CompletableFuture<R> requestReply(String topic, V value) {
        return adapt(sendAndReceive(topic, value));
    }

    @Override
    public CompletableFuture<R> requestReply(String topic, K key, V value) {
        return adapt(sendAndReceive(topic, key, value));
    }

    @Override
    public CompletableFuture<R> requestReply(String topic, Integer partition, K key, V value) {
        return adapt(sendAndReceive(topic, partition, key, value));
    }

    @Override
    public CompletableFuture<R> requestReply(String topic, Integer partition, Long timestamp, K key, V value) {
        return adapt(sendAndReceive(topic, partition, timestamp, key, value));
    }

    private CompletableFuture<R> adapt(RequestReplyFuture<K, V, R> requestReplyFuture) {
        CompletableFuture<R> completableResult = new CompletableFuture<R>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean result = requestReplyFuture.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return result;
            }
        };

        requestReplyFuture.getSendFuture().exceptionally(ex -> {
            completableResult.completeExceptionally(ex);
            return null;
        });

        requestReplyFuture.handle((result, ex) -> {
            if (ex == null) {
                completableResult.complete(result.value());
                return result;
            }
            else {
                completableResult.completeExceptionally(ex);
                return null;
            }
        });

        return completableResult;
    }
}