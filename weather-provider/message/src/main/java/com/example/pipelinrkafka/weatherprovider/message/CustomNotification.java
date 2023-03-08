package com.example.pipelinrkafka.weatherprovider.message;

import an.awesome.pipelinr.Notification;
import com.example.pipelinrkafka.messagebus.core.MessageBusRequest;

@MessageBusRequest(topic = Config.TOPIC_NAME)
public record CustomNotification() implements Notification {}
