package com.example.pipelinrkafka.weatherprovider.message;

import com.example.pipelinrkafka.weatherprovider.model.UnitOfMeasure;

public record GetTemperatureResponse(String city, double temperature, UnitOfMeasure unitOfMeasure) {
}
