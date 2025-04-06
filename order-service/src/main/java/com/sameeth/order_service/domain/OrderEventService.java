package com.sameeth.order_service.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sameeth.order_service.domain.models.OrderCancelledEvent;
import com.sameeth.order_service.domain.models.OrderCreatedEvent;
import com.sameeth.order_service.domain.models.OrderDeliveredEvent;
import com.sameeth.order_service.domain.models.OrderErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderEventService {
    private static final Logger log = LoggerFactory.getLogger(OrderEventService.class);
    private final OrderEventRepository repository;
    private final OrderEventPublisher publisher;
    private final ObjectMapper mapper;

    OrderEventService(OrderEventRepository repository, OrderEventPublisher publisher, ObjectMapper mapper) {
        this.repository = repository;
        this.publisher = publisher;
        this.mapper = mapper;
    }

    void save(OrderCreatedEvent event) {
        OrderEventEntity orderEvent = new OrderEventEntity();
        orderEvent.setEventId(event.eventId());
        orderEvent.setEventType(OrderEventType.ORDER_CREATED);
        orderEvent.setOrderNumber(event.orderNumber());
        orderEvent.setCreatedAt(event.createdAt());
        orderEvent.setPayload(toJsonPayload(event));
        this.repository.save(orderEvent);
    }

    void save(OrderDeliveredEvent event) {
        OrderEventEntity orderEvent = new OrderEventEntity();
        orderEvent.setEventId(event.eventId());
        orderEvent.setEventType(OrderEventType.ORDER_DELIVERED);
        orderEvent.setOrderNumber(event.orderNumber());
        orderEvent.setCreatedAt(event.createdAt());
        orderEvent.setPayload(toJsonPayload(event));
        this.repository.save(orderEvent);
    }

    void save(OrderCancelledEvent event) {
        OrderEventEntity orderEvent = new OrderEventEntity();
        orderEvent.setEventId(event.eventId());
        orderEvent.setEventType(OrderEventType.ORDER_CANCELLED);
        orderEvent.setOrderNumber(event.orderNumber());
        orderEvent.setCreatedAt(event.createdAt());
        orderEvent.setPayload(toJsonPayload(event));
        this.repository.save(orderEvent);
    }

    void save(OrderErrorEvent event) {
        OrderEventEntity orderEvent = new OrderEventEntity();
        orderEvent.setEventId(event.eventId());
        orderEvent.setEventType(OrderEventType.ORDER_PROCESSING_FAILED);
        orderEvent.setOrderNumber(event.orderNumber());
        orderEvent.setCreatedAt(event.createdAt());
        orderEvent.setPayload(toJsonPayload(event));
        this.repository.save(orderEvent);
    }

    public void publishOrderEvents() {
        Sort sort = Sort.by("createdAt").ascending();
        List<OrderEventEntity> events = repository.findAll(sort);
        log.info("Found {} Order Events to be published", events.size());
        for (var event: events) {
            log.info("Publishing event: {}", event.getEventId());
            this.publishEvent(event);
            repository.delete(event);
        }
    }

    private void publishEvent(OrderEventEntity event) {
        OrderEventType eventType = event.getEventType();
        switch (eventType){
            case ORDER_CREATED -> {
                OrderCreatedEvent orderCreatedEvent = fromJsonPayload(event.getPayload(), OrderCreatedEvent.class);
                publisher.publish(orderCreatedEvent);
                log.info("Event with id {} was published successfully", event.getEventId());
            }
            case ORDER_DELIVERED -> {
                OrderDeliveredEvent orderDeliveredEvent = fromJsonPayload(event.getPayload(), OrderDeliveredEvent.class);
                publisher.publish(orderDeliveredEvent);
            }

            case ORDER_CANCELLED -> {
                OrderCancelledEvent orderCancelledEvent = fromJsonPayload(event.getPayload(), OrderCancelledEvent.class);
                publisher.publish(orderCancelledEvent);
            }

            case ORDER_PROCESSING_FAILED -> {
                OrderErrorEvent orderErrorEvent = fromJsonPayload(event.getPayload(), OrderErrorEvent.class);
                publisher.publish(orderErrorEvent);
            }

            default -> {
                log.warn("Unsupported OrderEventType: {}", eventType);
            }
        }
    }
    
    private String toJsonPayload(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private <T> T fromJsonPayload(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
