package com.tikam.kafka_architecture.dto;

public record UserEvent(
        String userId,
        String eventType,
        long timestamp
) {}

