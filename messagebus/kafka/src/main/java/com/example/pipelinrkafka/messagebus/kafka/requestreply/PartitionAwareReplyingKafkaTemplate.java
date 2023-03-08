package com.example.pipelinrkafka.messagebus.kafka.requestreply;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.GenericMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.lang.Nullable;

/**
 * The {@link ReplyingKafkaTemplate} implementing the "reply-to" pattern in Kafka.
 * by setting the REPLY_TOPIC and REPLY_PARTITION headers.
 * See <a href="https://github.com/callistaenterprise/blog-synchronous-kafka">blog-synchronous-kafka</a>.
 */
public class PartitionAwareReplyingKafkaTemplate<K, V, R> extends ReplyingKafkaTemplate<K, V, R> implements PartitionAwareReplyingKafkaOperations<K, V, R> {

    public PartitionAwareReplyingKafkaTemplate(ProducerFactory<K, V> producerFactory,
                                               GenericMessageListenerContainer<K, R> replyContainer) {
        super(producerFactory, replyContainer);
    }

    private static byte[] intToBytesBigEndian(final int data) {
        return new byte[]{(byte) ((data >> 24) & 0xff), (byte) ((data >> 16) & 0xff),
                (byte) ((data >> 8) & 0xff), (byte) ((data >> 0) & 0xff),};
    }

    private TopicPartition getFirstAssignedReplyTopicPartition() {
        if (getAssignedReplyTopicPartitions() != null &&
                getAssignedReplyTopicPartitions().iterator().hasNext()) {
            TopicPartition replyPartition = getAssignedReplyTopicPartitions().iterator().next();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using partition " + replyPartition.partition());
            }
            return replyPartition;
        } else {
            throw new KafkaException("Illegal state: No reply partition is assigned to this instance");
        }
    }

    @Override
    public RequestReplyFuture<K, V, R> sendAndReceiveDefault(@Nullable V value) {
        return sendAndReceive(getDefaultTopic(), value);
    }

    @Override
    public RequestReplyFuture<K, V, R> sendAndReceiveDefault(K key, @Nullable V value) {
        return sendAndReceive(getDefaultTopic(), key, value);
    }

    @Override
    public RequestReplyFuture<K, V, R> sendAndReceiveDefault(Integer partition, K key, V value) {
        return sendAndReceive(getDefaultTopic(), partition, key, value);
    }

    @Override
    public RequestReplyFuture<K, V, R> sendAndReceiveDefault(Integer partition, Long timestamp, K key, V value) {
        return sendAndReceive(getDefaultTopic(), partition, timestamp, key, value);
    }

    @Override
    public RequestReplyFuture<K, V, R> sendAndReceive(String topic, @Nullable V value) {
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, value);
        return doSendAndReceive(record);
    }

    @Override
    public RequestReplyFuture<K, V, R> sendAndReceive(String topic, K key, @Nullable V value) {
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, key, value);
        return doSendAndReceive(record);
    }

    @Override
    public RequestReplyFuture<K, V, R> sendAndReceive(String topic, Integer partition, K key, V value) {
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, partition, key, value);
        return doSendAndReceive(record);
    }

    @Override
    public RequestReplyFuture<K, V, R> sendAndReceive(String topic, Integer partition, Long timestamp, K key, V value) {
        ProducerRecord<K, V> record = new ProducerRecord<>(topic, partition, timestamp, key, value);
        return doSendAndReceive(record);
    }

    @Override
    public RequestReplyFuture<K, V, R> sendAndReceive(ProducerRecord<K, V> record) {
        return doSendAndReceive(record);
    }

    protected RequestReplyFuture<K, V, R> doSendAndReceive(ProducerRecord<K, V> record) {
        TopicPartition replyPartition = getFirstAssignedReplyTopicPartition();
        record.headers()
                .add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyPartition.topic().getBytes()))
                .add(new RecordHeader(KafkaHeaders.REPLY_PARTITION,
                        intToBytesBigEndian(replyPartition.partition())));
        return super.sendAndReceive(record);
    }
}
