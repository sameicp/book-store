package com.sameeth.order_service.domain;

public class InvalidOrderException extends RuntimeException {
    InvalidOrderException(String message) {
        super(message);
    }
}
