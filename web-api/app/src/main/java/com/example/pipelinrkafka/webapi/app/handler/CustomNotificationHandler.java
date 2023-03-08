package com.example.pipelinrkafka.webapi.app.handler;

import an.awesome.pipelinr.Notification;
import com.example.pipelinrkafka.weatherprovider.message.CustomNotification;
import org.springframework.stereotype.Component;

@Component
public class CustomNotificationHandler implements Notification.Handler<CustomNotification> {
    @Override
    public void handle(CustomNotification notification) {
        String art = """
                             _   _  __ _           _   _            \s
                            | | (_)/ _(_)         | | (_)           \s
                 _ __   ___ | |_ _| |_ _  ___ __ _| |_ _  ___  _ __ \s
                | '_ \\ / _ \\| __| |  _| |/ __/ _` | __| |/ _ \\| '_ \\\s
                | | | | (_) | |_| | | | | (_| (_| | |_| | (_) | | | |
                |_| |_|\\___/ \\__|_|_| |_|\\___\\__,_|\\__|_|\\___/|_| |_|
                                                                    \s
                                                                    \s
                """;
        System.out.println(art);
    }
}
