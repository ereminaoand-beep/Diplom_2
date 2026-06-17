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

public class CreateUserTest {
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания нового пользователя")
    public void createUniqueUserTest() {
        String email = "test_" + System.currentTimeMillis() + "@example.com";
        user = new User(email, "password123", "TestUser");

        Response response = userClient.createUser(user);
        accessToken = userClient.getAccessToken(response);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo("TestUser"))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    @Description("Проверка ошибки при повторной регистрации")
    public void createExistingUserTest() {
        String email = "existing_" + System.currentTimeMillis() + "@example.com";
        user = new User(email, "password123", "ExistingUser");

        Response firstResponse = userClient.createUser(user);
        accessToken = userClient.getAccessToken(firstResponse);

        Response secondResponse = userClient.createUser(user);

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без заполнения обязательного поля")
    @Description("Проверка ошибки при отсутствии email")
    public void createUserWithoutRequiredFieldTest() {
        user = new User(null, "password123", "NoEmailUser");

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}