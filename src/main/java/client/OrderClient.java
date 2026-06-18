package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru";
    private static final String ORDERS = "/api/orders";

    @Step("Создание заказа с авторизацией")
    public Response createOrder(Order order, String accessToken) {
        return given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(BASE_URL + ORDERS);
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(Order order) {
        return given()
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post(BASE_URL + ORDERS);
    }
}