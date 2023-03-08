package com.example.pipelinrkafka.messagebus.kafka.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Configuration
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({
        KafkaConfiguration.class,
        KafkaRequestConsumerConfiguration.class,
        KafkaRequestProducerConfiguration.class
})
public @interface EnablePipelineOverKafka {
}
