package com.sameeth.order_service.web.controllers;

import com.sameeth.order_service.domain.OrderNotFoundException;
import com.sameeth.order_service.domain.OrderService;
import com.sameeth.order_service.domain.SecurityService;
import com.sameeth.order_service.domain.models.CreateOrderRequest;
import com.sameeth.order_service.domain.models.CreateOrderResponse;
import com.sameeth.order_service.domain.models.OrderDTO;
import com.sameeth.order_service.domain.models.OrderSummary;
import jakarta.validation.Valid;
import org.apache.catalina.LifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<OrderSummary> getOrders() {
        String userName = securityService.getLogInUserName();
        log.info("Fetching orders for user: {}", userName);
        return orderService.findOrders(userName);
    }

    @GetMapping(value = "/{orderNumber}")
    OrderDTO getOrder(@PathVariable(value = "orderNumber") String orderNumber) {
        log.info("Fetching order by id: {}", orderNumber);
        String userName = securityService.getLogInUserName();
        return orderService
                .findUserOrder(userName, orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
    }
}
