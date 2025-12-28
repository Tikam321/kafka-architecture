package com.tikam.kafka_architecture.service;

import com.tikam.kafka_architecture.dto.UserEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class LoadGenerator {

    private final EventProducer producer;

    public LoadGenerator(EventProducer producer) {
        this.producer = producer;
    }

//    @PostConstruct
//    public void generateLoad() {
//        for (int i = 0; i < 500_000; i++) {
//            producer.send(new UserEvent(
//                    "user-" + (i % 10_000),
//                    "CLICK",
//                    System.currentTimeMillis()
//            ));
//        }
//        System.out.println("Produced 500k events");
//    }
}
