package com.example.pipelinrkafka.messagebus.kafka;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Voidy;

/**
 * This class is responsible for handling requests for a pipeline. It processes different types of
 * requests, such as {@link Command} and {@link Notification}, and dispatches them through the
 * provided pipeline.
 */
public class PipelineRequestHandler {
    private final Pipeline pipeline;

    /**
     * Constructs a new {@link PipelineRequestHandler} with the given {@link Pipeline}.
     *
     * @param pipeline The pipeline used for sending commands and notifications.
     */
    public PipelineRequestHandler(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    /**
     * Processes the given request, which can be either a {@link Command} or a {@link Notification},
     * and sends it through the associated {@link Pipeline}. If the request is a {@link Command},
     * the method returns the result of the pipeline execution. If the request is a
     * {@link Notification}, the method sends the notification through the pipeline and returns a
     * {@link Voidy} object.
     *
     * @param request The request object, which can be a {@link Command} or a {@link Notification}.
     * @return An object representing the result of processing the request. For {@link Command}
     *         objects, this is the result of the pipeline execution. For {@link Notification}
     *         objects, this is a {@link Voidy} object.
     * @throws RuntimeException If the request type is neither a {@link Command} nor a
     *                          {@link Notification}.
     */
    public Object handle(Object request) throws RuntimeException {
        if (request instanceof Command<?> command) {
            return pipeline.send(command);
        }
        else if (request instanceof Notification notification) {
            pipeline.send(notification);
            return new Voidy();
        }

        throw new RuntimeException(String.format("Unknown request type: %s", request.getClass().getName()));
    }
}
