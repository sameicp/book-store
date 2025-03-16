package com.sameeth.catalog_service;

import org.springframework.boot.SpringApplication;

public class TestCatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(CatalogServiceApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
