package com.sameeth.order_service.web.controllers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListener {
    @RabbitListener(queues = "${orders.new-orders-queue}")
    public void handleNewOrder(MyPayload payload) {
        System.out.println("New order: " + payload.content());
    }

    @RabbitListener(queues = "${orders.delivered-orders-queue}")
    public void handleDeliveredOrder(MyPayload payload) {
        System.out.println("Delivered order: " + payload.content());
    }
}
