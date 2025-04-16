package com.sameeth.notification_service.events;

import com.sameeth.notification_service.domain.NotificationService;
import com.sameeth.notification_service.domain.OrderEventEntity;
import com.sameeth.notification_service.domain.OrderEventRepository;
import com.sameeth.notification_service.domain.models.OrderCancelledEvent;
import com.sameeth.notification_service.domain.models.OrderCreatedEvent;
import com.sameeth.notification_service.domain.models.OrderDeliveredEvent;
import com.sameeth.notification_service.domain.models.OrderErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderEventHandler.class);
    private final NotificationService notificationService;
    private final OrderEventRepository orderEventRepository;

    OrderEventHandler(NotificationService notificationService, OrderEventRepository orderEventRepository) {
        this.notificationService = notificationService;
        this.orderEventRepository = orderEventRepository;
    }

    @RabbitListener(queues = "${notifications.new-orders-queue}")
    void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Order Created Event: {}", event);
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Received Duplicate OrderCreatedEvent with eventId: {}", event.eventId());
            return;
        }
        notificationService.sendOrderCreatedNotification(event);
        OrderEventEntity orderEventEntity = new OrderEventEntity(event.eventId());
        orderEventRepository.save(orderEventEntity);
    }

    @RabbitListener(queues = "${notifications.delivered-orders-queue}")
    void handleOrderCreatedEvent(OrderDeliveredEvent event) {
        log.info("Order Delivered Event: {}", event);
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Received Duplicate OrderDeliveredEvent with eventId: {}", event.eventId());
            return;
        }
        notificationService.sendOrderDeliveredNotification(event);
        OrderEventEntity orderEventEntity = new OrderEventEntity(event.eventId());
        orderEventRepository.save(orderEventEntity);
    }

    @RabbitListener(queues = "${notifications.cancelled-orders-queue}")
    void handleOrderCreatedEvent(OrderCancelledEvent event) {
        log.info("Order Cancelled Event: {}", event);
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Received Duplicate OrderCancelledEvent with eventId: {}", event.eventId());
            return;
        }
        notificationService.sendOrderCancelledNotification(event);
        OrderEventEntity orderEventEntity = new OrderEventEntity(event.eventId());
        orderEventRepository.save(orderEventEntity);
    }

    @RabbitListener(queues = "${notifications.error-orders-queue}")
    void handleOrderCreatedEvent(OrderErrorEvent event) {
        log.info("Order Error Event: {}", event);
        if (orderEventRepository.existsByEventId(event.eventId())) {
            log.warn("Received Duplicate OrderErrorEvent with eventId: {}", event.eventId());
            return;
        }
        notificationService.sendOrderErrorEventNotification(event);
        OrderEventEntity orderEventEntity = new OrderEventEntity(event.eventId());
        orderEventRepository.save(orderEventEntity);
    }

}
