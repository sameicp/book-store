package com.sameeth.catalog_service.web.controllers;

import com.sameeth.catalog_service.AbstractIT;
import com.sameeth.catalog_service.domain.Product;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

@Sql("/test-data.sql")
class ProductControllerTest extends AbstractIT {
    @Test
    void shouldReturnProducts() {
        RestAssured.given().contentType(ContentType.JSON)
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("data", Matchers.hasSize(10))
                .body("totalElements", Matchers.is(15))
                .body("pageNumber", Matchers.is(1))
                .body("totalPages", Matchers.is(2))
                .body("isFirst", Matchers.is(true))
                .body("isLast", Matchers.is(false))
                .body("hasNext", Matchers.is(true))
                .body("hasPrevious", Matchers.is(false));
    }

    @Test
    void shouldGetProductByCode() {
        Product product = RestAssured.given().contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", "GR009")
                .then()
                .statusCode(200)
                .assertThat()
                .extract()
                .body()
                .as(Product.class);
        Assertions.assertThat(product.code()).isEqualTo("GR009");
        Assertions.assertThat(product.name()).isEqualTo("The Chronicles of Narnia");
        Assertions.assertThat(product.description()).isEqualTo("A series of fantasy novels where children discover a magical world accessible through a wardrobe.");
        Assertions.assertThat(product.price()).isEqualTo(BigDecimal.valueOf(15.99));
    }

    @Test
    void shouldReturnNotFoundWhenProductCodeNotExist() {
        String code = "same";
        RestAssured.given().contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", code)
                .then()
                .statusCode(404)
                .body("status", Matchers.is(404))
                .body("title", Matchers.is("Product Not Found"))
                .body("detail", Matchers.is("Product with code " + code + " not found."));
    }
}
