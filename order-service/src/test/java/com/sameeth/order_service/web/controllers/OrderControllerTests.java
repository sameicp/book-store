package com.sameeth.order_service.web.controllers;

import com.sameeth.order_service.AbstractIT;
import com.sameeth.order_service.domain.models.OrderSummary;
import com.sameeth.order_service.testdata.TestDataFactory;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql("/test-orders.sql")
class OrderControllerTests extends AbstractIT {

    @Nested
    class GetOrdersTests {

        @Test
        void shouldGetOrdersSuccessfully() {
            List<OrderSummary> orderSummaries = RestAssured.given()
                    .when()
                    .get("/api/orders")
                    .then()
                    .log()
                    .all()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(new TypeRef<>() {});

            Assertions.assertThat(orderSummaries).hasSize(2);
        }
    }

    @Nested
    class CreateOrderTests {
        @Test
        void shouldCreateOrderSuccessfully() {
            mockGetProductByCode("same", "Product 1", BigDecimal.valueOf(12.1));
            var payload =
                    """
                            {
                                "customer": {
                                    "name": "Same",
                                    "email": "same@gmail.com",
                                    "phone": "0771212234"
                                },
                                "deliveryAddress": {
                                    "addressLine1": "Mbizo",
                                    "addressLine2": "Amavheni",
                                    "city": "Kwekwe",
                                    "state": "Midlands",
                                    "zipCode": "00000",
                                    "country": "Zimbabwe"
                                },
                                "items": [
                                    {
                                        "code": "same",
                                        "name": "Product 1",
                                        "price": 12.1,
                                        "quantity": 1
                                    }
                                ]
                            }
                            """;
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("orderNumber", CoreMatchers.notNullValue());
        }

        @Test
        void shouldReturnBadRequestWhenMandatoryDataIsMissing() {
            var payload = TestDataFactory.createOrderRequestWithInvalidCustomer();
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(payload)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class GetOrderByOrderNumberTests {
        String orderNumber = "order-123";

        @Test
        void shouldGetOrderSuccessfully() {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/api/orders/{orderNumber}", orderNumber)
                    .then()
                    .statusCode(200)
                    .body("orderNumber", CoreMatchers.is(orderNumber))
                    .body("items.size()", CoreMatchers.is(2));
        }
    }
}
