package com.example.pipelinrkafka.messagebus.kafka;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import com.example.pipelinrkafka.messagebus.core.ExceptionResponse;
import com.example.pipelinrkafka.messagebus.core.MessageBusRequest;
import com.example.pipelinrkafka.messagebus.kafka.requestreply.CompletableFutureReplyingKafkaOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.util.StringValueResolver;

import java.util.concurrent.ExecutionException;

/**
 * A decorator of {@link Pipeline} that adds Kafka message bus support for processing commands.
 */
public class KafkaRoutingPipeline implements Pipeline {
    @Autowired
    private CompletableFutureReplyingKafkaOperations<String, Object, Object> requestReplyKafkaTemplate;
    @Autowired
    private StringValueResolver stringValueResolver;
    private final Pipeline pipeline;

    public KafkaRoutingPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public KafkaRoutingPipeline(Pipeline pipeline, CompletableFutureReplyingKafkaOperations<String, Object, Object> requestReplyKafkaTemplate, StringValueResolver stringValueResolver) {
        this.pipeline = pipeline;
        this.requestReplyKafkaTemplate = requestReplyKafkaTemplate;
        this.stringValueResolver = stringValueResolver;
    }

    /**
     * Sends the given command for processing. If the command is a message bus request,
     * it is sent over Kafka. Otherwise, it is processed locally.
     *
     * @param command the command to process
     * @return the result of processing the command
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R, C extends Command<R>> R send(C command) {
        if (this.isMessageBusRequest(command)) {
            return (R)sendOverKafka(command);
        }
        else {
            return pipeline.send(command);
        }
    }

    /**
     * Sends the given notification for processing. If the notification is a message bus request,
     * it is sent over Kafka. Otherwise, it is processed locally.
     *
     * @param notification the notification to process
     */
    @Override
    public <N extends Notification> void send(N notification) {
        pipeline.send(notification);

        if (this.isMessageBusRequest(notification)) {
            sendOverKafka(notification);
        }
    }

    /**
     * Determines if the given request is a message bus request based on the
     * presence of the {@link MessageBusRequest} annotation.
     *
     * @param request the request to check.
     * @return true if the request is a message bus request, false otherwise.
     */
    private boolean isMessageBusRequest(Object request) {
        return request.getClass().getDeclaredAnnotation(MessageBusRequest.class) != null;
    }

    /**
     * Retrieves the request topic for the given request based on the
     * {@link MessageBusRequest} annotation.
     *
     * @param request the request to get the topic for.
     * @return the resolved topic for the request.
     */
    private String getRequestTopic(Object request) {
        var annotation = request.getClass().getAnnotation(MessageBusRequest.class);
        if (annotation == null) {
            throw new AnnotationConfigurationException(
                    String.format(
                            "The class %s must have annotation %s",
                            request.getClass().getSimpleName(),
                            MessageBusRequest.class.getSimpleName()
                    )
            );
        }
        return this.stringValueResolver.resolveStringValue(annotation.topic());
    }

    /**
     * Sends the given request over Kafka for processing and returns the result.
     *
     * @param request the request to send over Kafka.
     * @return the result of processing the command.
     */
    private Object sendOverKafka(Object request) {
        var requestTopic = this.getRequestTopic(request);
        try {
            var response = requestReplyKafkaTemplate.requestReply(requestTopic, request).get();
            if (response instanceof ExceptionResponse) {
                throw new RuntimeException("Error processing the request.", ((ExceptionResponse) response).getException());
            }
            return response;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Unable to send the request or get the response from kafka.", e);
        }
    }
}
