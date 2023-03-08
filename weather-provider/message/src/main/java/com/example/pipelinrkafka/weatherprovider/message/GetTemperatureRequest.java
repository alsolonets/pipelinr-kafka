package com.example.pipelinrkafka.weatherprovider.message;

import an.awesome.pipelinr.Command;
import com.example.pipelinrkafka.messagebus.core.MessageBusRequest;
import com.example.pipelinrkafka.weatherprovider.model.UnitOfMeasure;

@MessageBusRequest(topic = Config.TOPIC_NAME)
public record GetTemperatureRequest(String city, UnitOfMeasure unitOfMeasure) implements Command<GetTemperatureResponse> {}
