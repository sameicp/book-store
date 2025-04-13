package com.sameeth.notification_service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notifications")
public record ApplicationProperties(
        String orderEventsExchange,
        String newOrdersQueue,
        String deliveryOrdersQueue,
        String cancelledOrdersQueue,
        String errorOrdersQueue
){
}
