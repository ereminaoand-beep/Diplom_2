package user;

import client.UserClient;
import model.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class LoginUserTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        String email = "logintest_" + System.currentTimeMillis() + "@example.com";
        user = new User(email, "password123", "LoginUser");

        Response createResponse = userClient.createUser(user);
        accessToken = userClient.getAccessToken(createResponse);
    }

    @After
    public void tearDown() {
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    @Description("Проверка успешного входа с корректными данными")
    public void loginExistingUserTest() {
        Response response = userClient.loginUser(user);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем")
    @Description("Проверка ошибки при неправильных учётных данных")
    public void loginWithInvalidCredentialsTest() {
        User invalidUser = new User("wrong@example.com", "wrongpassword", null);

        Response response = userClient.loginUser(invalidUser);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}