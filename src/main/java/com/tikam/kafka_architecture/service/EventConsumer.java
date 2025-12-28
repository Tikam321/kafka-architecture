package com.tikam.kafka_architecture.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EventConsumer {

    @KafkaListener(
            topics = "${app.kafka.topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            ConsumerRecord<String, String> record,
            Acknowledgment ack) throws InterruptedException {

//        System.out.printf(
//                "Consumed | partition=%d offset=%d key=%s%n ",
//                record.partition(),
//                record.offset(),
//                record.key()
//        );

        // 🔥 INTENTIONAL FAILURE
//        if ("8291".equalsIgnoreCase(record.key())) {
//            System.out.println("❌ Simulated failure for message");
//            throw new RuntimeException("Simulated consumer crash");
//        }

        // 🔥 Simulate slow processing
//        Thread.sleep(5000);

//        System.out.println("✅ Processed successfully");
        long eventTime = record.timestamp(); // Kafka record timestamp
        long now = System.currentTimeMillis();
        long maxTime = 0;

        long lagTimeMs = now - eventTime;
        maxTime = Math.max(maxTime, lagTimeMs);

        System.out.println(
                "Partition=" + record.partition() +
                        " Offset=" + record.offset() +
                        " LagTime(ms)=" + maxTime
        );


        ack.acknowledge();
    }
}
