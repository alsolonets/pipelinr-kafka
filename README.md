Welcome to the Pipelinr Kafka Demo Project! 
This is an example that demonstrates the use of 
Pipelinr, Kafka, and Spring Boot to build a message-driven microservice application. 
Please note that this project is for demonstration purposes only. 

# Features

* Message-driven architecture using Pipelinr, Kafka, and Spring Boot.
* Asynchronous request processing with configurable concurrency.
* Weather API example with a simple request-response flow.

# Prerequisites

Ensure that you have Kafka installed and running on your machine (see https://kafka.apache.org/quickstart).

# Running the Services

First, navigate to the project directory and use the following commands to run 2 applications using Gradle:

### Web API
```bash
./gradlew :web-api:app:bootRun
```

### Weather Provider
```bash
./gradlew :weather-provider:app:bootRun
```

# Testing Command and Notification Processing

### Command

```bash
curl http://localhost:8080/weather?city=London&unitOfMeasure=Celsius
```

Output:
```json
{"city":"London","temperature":22.75636800328681,"unitOfMeasure":"Celsius"}
```

### Notification

```bash
curl -X POST http://localhost:8080/notify
```

Observe in both `Weather Provider` and `Web API` consoles:
```
             _   _  __ _           _   _
            | | (_)/ _(_)         | | (_)
 _ __   ___ | |_ _| |_ _  ___ __ _| |_ _  ___  _ __
| '_ \ / _ \| __| |  _| |/ __/ _` | __| |/ _ \| '_ \
| | | | (_) | |_| | | | | (_| (_| | |_| | (_) | | | |
|_| |_|\___/ \__|_|_| |_|\___\__,_|\__|_|\___/|_| |_|
```

# Testing Concurrent Request Processing

Run 5 instances of `Weather Provider`. This number corresponds to the `example.messagebus.kafka.topic.self.partitions`
of `Wather Provider's` application.yml.

```bash
ab -n 100 -c 50 http://localhost:8080/weather?city=London&unitOfMeasure=Celsius
```
This command sends 100 requests to the Weather API with a concurrency of 50.

# Sources and Links

[Synchronous Kafka project](https://github.com/callistaenterprise/blog-synchronous-kafka): The primary inspiration for this project.

# License

This project is open source and available under the Apache License.