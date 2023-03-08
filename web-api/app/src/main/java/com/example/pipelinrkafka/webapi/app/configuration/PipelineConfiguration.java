package com.example.pipelinrkafka.webapi.app.configuration;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;
import com.example.pipelinrkafka.messagebus.kafka.KafkaRoutingPipeline;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PipelineConfiguration {
    @Bean
    Pipeline pipeline(ObjectProvider<Command.Handler> commandHandlers, ObjectProvider<Notification.Handler> notificationHandlers, ObjectProvider<Command.Middleware> middlewares) {
        var pipelinr = new Pipelinr()
                .with(commandHandlers::stream)
                .with(notificationHandlers::stream)
                .with(middlewares::orderedStream);

        return new KafkaRoutingPipeline(pipelinr);
    }
}
