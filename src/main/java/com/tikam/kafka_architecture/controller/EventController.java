package com.tikam.kafka_architecture.controller;

import com.tikam.kafka_architecture.dto.UserEvent;
import com.tikam.kafka_architecture.service.EventProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventProducer producer;

    public EventController(EventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<Void> publish(@RequestParam String userId, @RequestParam String event){
        producer.send(new UserEvent(userId, event, System.currentTimeMillis()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/load")
    public ResponseEntity<Void> load(@RequestParam int count) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            producer.send(new UserEvent(
                    "user-" + (i % 10_000),
                    "CLICK",
                    System.currentTimeMillis()
            ));
        }
        long end = System.currentTimeMillis();
        System.out.println("Produced " + count + " in " + (end - start) + " ms");
        double tps = count / ((end - start) / 1000.0);
        System.out.println("Throughput = " + tps + " msg/sec");
        return ResponseEntity.ok().build();
    }
}
