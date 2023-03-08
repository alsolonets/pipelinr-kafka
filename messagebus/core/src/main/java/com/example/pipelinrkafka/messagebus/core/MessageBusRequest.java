package com.example.pipelinrkafka.messagebus.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The {@code MessageBusRequest} annotation is used to annotate request classes that should be
 * processed using the message bus system. It provides metadata, such as the Kafka topic to which
 * the request should be sent.
 * <p>
 * Classes annotated with {@code MessageBusRequest} should implement the {@link an.awesome.pipelinr.Command}
 * or {@link an.awesome.pipelinr.Notification} interfaces, as they are expected to be used in conjunction
 * with the Pipelinr command pipeline.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageBusRequest {
    /**
     * Topic name, e.g. 'topic.name' or '${config.topic}'.
     */
    String topic();
}
