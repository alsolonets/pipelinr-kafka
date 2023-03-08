package com.example.pipelinrkafka.webapi.app.controller;

import an.awesome.pipelinr.Pipeline;
import com.example.pipelinrkafka.weatherprovider.message.CustomNotification;
import com.example.pipelinrkafka.weatherprovider.message.GetTemperatureRequest;
import com.example.pipelinrkafka.weatherprovider.message.GetTemperatureResponse;
import com.example.pipelinrkafka.weatherprovider.model.UnitOfMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {
    @Autowired
    Pipeline pipeline;

    @GetMapping("/weather")
    public GetTemperatureResponse getTemperature(
            @RequestParam String city,
            @RequestParam(defaultValue = "Celsius") UnitOfMeasure unitOfMeasure) {
        var response = pipeline.send(new GetTemperatureRequest(city, unitOfMeasure));
        return response;
    }

    @PostMapping("/notify")
    public void sendNotification() {
        pipeline.send(new CustomNotification());
    }
}
