package com.example.pipelinrkafka.weatherprovider.app.handler;

import an.awesome.pipelinr.Command;
import com.example.pipelinrkafka.weatherprovider.message.GetTemperatureRequest;
import com.example.pipelinrkafka.weatherprovider.message.GetTemperatureResponse;
import com.example.pipelinrkafka.weatherprovider.model.UnitOfMeasure;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Random;

@Component
public class GetTemperatureRequestHandler implements Command.Handler<GetTemperatureRequest, GetTemperatureResponse> {
    private final Random random = new Random(42);
    private final Duration delay = Duration.ofSeconds(1);

    @Override
    public GetTemperatureResponse handle(GetTemperatureRequest command) {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        var temperature = -10 + (random.nextDouble() * 100) % 40;
        if (command.unitOfMeasure() == UnitOfMeasure.Fahrenheit) {
            temperature = celsiusToFahrenheit(temperature);
        }
        return new GetTemperatureResponse(command.city(), temperature, command.unitOfMeasure());
    }

    private double celsiusToFahrenheit(double celsius) {
        return (celsius * 9/5) + 32;
    }
}
