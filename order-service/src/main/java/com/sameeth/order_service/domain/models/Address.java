package com.sameeth.order_service.domain.models;

import jakarta.validation.constraints.NotBlank;

public record Address(
        @NotBlank(message = "Delivery address line is required") String addressLine1,
        String addressLine2,
        @NotBlank(message = "Delivery city is required") String city,
        @NotBlank(message = "Delivery state is required") String state,
        @NotBlank(message = "Delivery zip code is required") String zipCode,
        @NotBlank(message = "Delivery country is required") String country) {}
