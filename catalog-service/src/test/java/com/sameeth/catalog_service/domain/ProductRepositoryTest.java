package com.sameeth.catalog_service.domain;

// WE SHOULD NOT TEST FRAMEWORK FEATURES. THIS IS FOR LEARNING PURPOSES ONLY

import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest(
        properties = {
            "spring.test.database.replace=none",
            "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db",
        })
// @Import(TestcontainersConfiguration.class)
@Sql("/test-data.sql")
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    // The test below is not required because the method is already tested by the framework.
    @Test
    void shouldGetAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        Assertions.assertThat(products).hasSize(15);
    }

    @Test
    void shouldGetProductByCode() {
        ProductEntity product = productRepository.findByCode("GR009").orElseThrow();
        Assertions.assertThat(product.getCode()).isEqualTo("GR009");
        Assertions.assertThat(product.getName()).isEqualTo("The Chronicles of Narnia");
        Assertions.assertThat(product.getDescription())
                .isEqualTo(
                        "A series of fantasy novels where children discover a magical world accessible through a wardrobe.");
        Assertions.assertThat(product.getPrice()).isEqualTo(BigDecimal.valueOf(15.99));
    }

    @Test
    void shouldReturnEmptyWhenProductCodeNotExists() {
        Assertions.assertThat(productRepository.findByCode("same")).isEmpty();
    }
}
