package com.sameeth.order_service.domain.models;

import com.sameeth.order_service.domain.OrderStatus;

public record OrderSummary(
        String orderNumber,
        OrderStatus status
) {
}
