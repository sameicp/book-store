package com.sameeth.order_service.web.controllers;

import com.sameeth.order_service.domain.OrderService;
import com.sameeth.order_service.domain.SecurityService;
import com.sameeth.order_service.domain.models.CreateOrderRequest;
import com.sameeth.order_service.domain.models.CreateOrderResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/orders")
class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final SecurityService securityService;

    OrderController(OrderService orderService, SecurityService securityService) {
        this.orderService = orderService;
        this.securityService = securityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String logInUserName = securityService.getLogInUserName();
        log.info("Creating order for user: {}", logInUserName);
        return orderService.createOrder(logInUserName, request);
    }
}
