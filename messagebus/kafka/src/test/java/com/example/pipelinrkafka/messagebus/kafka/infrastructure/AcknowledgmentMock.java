package com.example.pipelinrkafka.messagebus.kafka.infrastructure;

import org.springframework.kafka.support.Acknowledgment;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class AcknowledgmentMock implements Acknowledgment {
    private final AtomicInteger ackCount = new AtomicInteger();
    private final AtomicInteger nackCount = new AtomicInteger();

    /**
     * Invoked when the record or batch for which the acknowledgment has been created has
     * been processed. Calling this method implies that all the previous messages in the
     * partition have been processed already.
     */
    @Override
    public void acknowledge() {
        ackCount.incrementAndGet();
    }

    /**
     * Negatively acknowledge the current record - discard remaining records from the poll
     * and re-seek all partitions so that this record will be redelivered after the sleep
     * time. Must be called on the consumer thread.
     * <p>
     * <b>When using group management,
     * {@code sleep + time spent processing the previous messages from the poll} must be
     * less than the consumer {@code max.poll.interval.ms} property, to avoid a
     * rebalance.</b>
     *
     * @param sleep the time to sleep.
     * @since 2.3
     */
    @Override
    public void nack(Duration sleep) {
        this.nackCount.incrementAndGet();
    }

    /**
     * Negatively acknowledge the record at an index in a batch - commit the offset(s) of
     * records before the index and re-seek the partitions so that the record at the index
     * and subsequent records will be redelivered after the sleep time. Must be called on
     * the consumer thread.
     * <p>
     * <b>When using group management,
     * {@code sleep + time spent processing the records before the index} must be less
     * than the consumer {@code max.poll.interval.ms} property, to avoid a rebalance.</b>
     *
     * @param index the index of the failed record in the batch.
     * @param sleep the time to sleep.
     * @since 2.3
     */
    @Override
    public void nack(int index, Duration sleep) {
        this.nack(sleep);
    }

    /**
     * Gets the number of acknowledgments.
     */
    public Integer getAckCount() {
        return ackCount.get();
    }

    /**
     * Gets the number of negative acknowledgments.
     */
    public Integer getNackCount() {
        return nackCount.get();
    }
}
