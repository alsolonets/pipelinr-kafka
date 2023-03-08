package com.example.pipelinrkafka.webapi.app;

import com.example.pipelinrkafka.messagebus.kafka.configuration.EnablePipelineOverKafka;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnablePipelineOverKafka
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
