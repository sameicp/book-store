package com.sameeth.order_service.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sameeth.order_service.domain.OrderService;
import com.sameeth.order_service.domain.SecurityService;
import com.sameeth.order_service.domain.models.CreateOrderRequest;
import com.sameeth.order_service.testdata.TestDataFactory;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Stream;

@WebMvcTest(OrderController.class)
public class OrderControllerUnitTests {
    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private SecurityService securityService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    void setUp() {
        BDDMockito.given(securityService.getLogInUserName()).willReturn("same");
    }

    @ParameterizedTest(name = "[{index}]-{0}")
    @MethodSource("createOrderRequestProvider")
    void shouldReturnBadRequestWhenOrderPayloadIsInvalid(CreateOrderRequest request) throws Exception {
        BDDMockito.given(orderService.createOrder(ArgumentMatchers.eq("same"), ArgumentMatchers.any(CreateOrderRequest.class)))
                .willReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    static Stream<Arguments> createOrderRequestProvider() {
        return Stream.of(
                Arguments.arguments(Named.named("Order with Invalid Customer", TestDataFactory.createOrderRequestWithInvalidCustomer())),
                Arguments.arguments(Named.named("Order with Invalid delivered address", TestDataFactory.createOrderRequestWithInvalidDeliveryAddress())),
                Arguments.arguments(Named.named("Order with no Items", TestDataFactory.createOrderRequestWithNoItems())));
    }
}
