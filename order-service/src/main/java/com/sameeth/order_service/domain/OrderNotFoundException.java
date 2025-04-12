package com.sameeth.order_service.domain;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }

    public static OrderNotFoundException forCodeNumber(String orderNumber) {
        return new OrderNotFoundException("Order number with " + orderNumber + " not found");
    }
}
