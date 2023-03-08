package com.example.pipelinrkafka.messagebus.kafka.configuration;

import com.example.pipelinrkafka.messagebus.kafka.FailedDeserializationHandler;
import com.example.pipelinrkafka.messagebus.kafka.requestreply.CompletableFutureReplyingKafkaOperations;
import com.example.pipelinrkafka.messagebus.kafka.requestreply.CompletableFutureReplyingKafkaTemplate;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.StringValueResolver;

import java.time.Duration;

@Configuration
@ConditionalOnBean(KafkaConfiguration.class)
@ConditionalOnProperty("example.messagebus.kafka.topic.self.reply")
public class KafkaRequestProducerConfiguration {
    @Value("${example.messagebus.kafka.topic.self.reply}")
    public String replyTopic;

    @Autowired
    KafkaConfiguration kafkaConfiguration;

    @Bean
    public ProducerFactory<String, Object> requestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaConfiguration.producerConfigs());
    }

    @Bean
    public KafkaMessageListenerContainer<String, Object> replyListenerContainer() {
        ContainerProperties containerProperties = new ContainerProperties(replyTopic);
        return new KafkaMessageListenerContainer<>(replyConsumerFactory(), containerProperties);
    }

    @Bean
    public CompletableFutureReplyingKafkaOperations<String, Object, Object> replyingKafkaTemplate() {
        CompletableFutureReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate =
                new CompletableFutureReplyingKafkaTemplate<>(
                        requestProducerFactory(),
                        replyListenerContainer());
        replyingKafkaTemplate.setDefaultReplyTimeout(Duration.ofMillis(kafkaConfiguration.replyTimeoutMillis));
        return replyingKafkaTemplate;
    }

    @Bean
    public StringValueResolver stringValueResolver(@Autowired ConfigurableBeanFactory beanFactory) {
        return new EmbeddedValueResolver(beanFactory);
    }

    @Bean
    public ConsumerFactory<String, Object> replyConsumerFactory() {
        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("an.awesome.pipelinr", "com.example.pipelinrkafka.weatherprovider.message");
        jsonDeserializer.dontRemoveTypeHeaders();
        var deserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);
        deserializer.setFailedDeserializationFunction(FailedDeserializationHandler::handleResponseDeserializationError);
        return new DefaultKafkaConsumerFactory<>(
                kafkaConfiguration.consumerConfigs(),
                new StringDeserializer(),
                deserializer);
    }

    @Bean
    public NewTopic replyTopic() {
        return TopicBuilder
                .name(replyTopic)
                .partitions(kafkaConfiguration.partitionsCount)
                .replicas(kafkaConfiguration.REQUEST_REPLY_TOPIC_REPLICA_COUNT)
                .config(kafkaConfiguration.RETENTION_MS_CONFIG, kafkaConfiguration.replyTimeoutMillis.toString())
                .build();
    }
}
