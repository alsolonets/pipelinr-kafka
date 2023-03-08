package com.example.pipelinrkafka.messagebus.kafka.configuration;

import an.awesome.pipelinr.Pipeline;
import com.example.pipelinrkafka.messagebus.kafka.FailedDeserializationHandler;
import com.example.pipelinrkafka.messagebus.kafka.PipelineRequestHandler;
import com.example.pipelinrkafka.messagebus.kafka.RequestListener;
import com.example.pipelinrkafka.messagebus.kafka.MessageProcessorImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;

@Configuration
@ConditionalOnBean(KafkaConfiguration.class)
@ConditionalOnProperty("example.messagebus.kafka.topic.self.request")
public class KafkaRequestConsumerConfiguration {
    @Value("${example.messagebus.kafka.topic.self.request}")
    public String requestTopic;
    @Value("${example.messagebus.kafka.concurrency}")
    public Integer concurrency;

    private final Duration pollTimeout = Duration.ofSeconds(1);

    @Autowired
    Pipeline pipeline;

    @Autowired
    KafkaConfiguration kafkaConfiguration;

    @Bean
    public ConsumerFactory<String, Object> requestConsumerFactory() {
        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("an.awesome.pipelinr", "com.example.pipelinrkafka.weatherprovider.message");
        jsonDeserializer.dontRemoveTypeHeaders();
        var deserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);
        deserializer.setFailedDeserializationFunction(FailedDeserializationHandler::handleRequestDeserializationError);
        return new DefaultKafkaConsumerFactory<>(
                kafkaConfiguration.consumerConfigs(),
                new StringDeserializer(),
                deserializer);
    }

    @Bean
    public MessageListenerContainer messageListenerContainer() {
        var containerProps = new ContainerProperties(requestTopic);
        containerProps.setAckMode(ContainerProperties.AckMode.MANUAL);
        containerProps.setPollTimeout(pollTimeout.toMillis());
        var requestListener = kafkaRequestListener();
        containerProps.setMessageListener(requestListener);
        return new KafkaMessageListenerContainer<>(
                requestConsumerFactory(),
                containerProps);
    }

    @Bean
    public ProducerFactory<String, Object> replyProducerFactory() {
        var mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        var serializer = new JsonSerializer<>(mapper);
        return new DefaultKafkaProducerFactory<>(
                kafkaConfiguration.producerConfigs(),
                null,
                serializer);
    }

    @Bean
    public KafkaTemplate<String, Object> replyTemplate() {
        return new KafkaTemplate<>(replyProducerFactory());
    }

    @Bean
    public RequestListener kafkaRequestListener() {
        var pipelineRequestHandler = new PipelineRequestHandler(pipeline);
        var messageProcessor = new MessageProcessorImpl(
                pipelineRequestHandler::handle,
                Duration.ofMillis(kafkaConfiguration.replyTimeoutMillis),
                concurrency);
        var requestListener = new RequestListener(messageProcessor);
        requestListener.setReplyTemplate(replyTemplate());
        return requestListener;
    }

    @Bean
    public NewTopic requestTopic() {
        return TopicBuilder
                .name(requestTopic)
                .partitions(kafkaConfiguration.partitionsCount)
                .replicas(kafkaConfiguration.REQUEST_REPLY_TOPIC_REPLICA_COUNT)
                .config(kafkaConfiguration.RETENTION_MS_CONFIG, kafkaConfiguration.replyTimeoutMillis.toString())
                .build();
    }
}
