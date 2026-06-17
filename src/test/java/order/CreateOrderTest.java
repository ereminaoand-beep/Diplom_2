package order;

import client.OrderClient;
import client.UserClient;
import model.Order;
import model.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class CreateOrderTest {
    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();

        String email = "ordertest_" + System.currentTimeMillis() + "@example.com";
        user = new User(email, "password123", "OrderUser");
        Response createResponse = userClient.createUser(user);
        accessToken = userClient.getAccessToken(createResponse);
    }

    @After
    public void tearDown() {

    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа авторизованным пользователем")
    public void createOrderWithAuthTest() {
        List<String> ingredients = Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f"
        );
        Order order = new Order(ingredients);

        Response response = orderClient.createOrder(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа неавторизованным пользователем")
    public void createOrderWithoutAuthTest() {
        List<String> ingredients = Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f"
        );
        Order order = new Order(ingredients);

        Response response = orderClient.createOrderWithoutAuth(order);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    @Description("Проверка создания заказа с корректными ингредиентами")
    public void createOrderWithIngredientsTest() {
        List<String> ingredients = Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",
                "61c0c5a71d1f82001bdaaa6f",
                "61c0c5a71d1f82001bdaaa70"
        );
        Order order = new Order(ingredients);

        Response response = orderClient.createOrder(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.ingredients", hasSize(3));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка ошибки при пустом списке ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        Order order = new Order(null);

        Response response = orderClient.createOrder(order, accessToken);

        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка ошибки при использовании несуществующих id ингредиентов")
    public void createOrderWithInvalidIngredientHashTest() {
        List<String> invalid = Arrays.asList("invalid1", "invalid2");
        Order order = new Order(invalid);

        Response response = orderClient.createOrder(order, accessToken);
        response.then().statusCode(anyOf(is(500), is(400)));
    }
}