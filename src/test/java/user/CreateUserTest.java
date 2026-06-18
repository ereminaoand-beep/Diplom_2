package user;

import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
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
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(accessToken);
        }
    }

    @Step("Регистрация пользователя с email: {email}")
    private void registerUser(String email, String password, String name) {
        user = new User(email, password, name);
        Response response = userClient.createUser(user);
        accessToken = userClient.getAccessToken(response);
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
                .statusCode(SC_OK)
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
        registerUser(email, "password123", "ExistingUser");


        Response secondResponse = userClient.createUser(user);

        secondResponse.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка ошибки при отсутствии email")
    public void createUserWithoutEmailTest() {
        user = new User(null, "password123", "NoEmailUser");
        Response response = userClient.createUser(user);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка ошибки при отсутствии пароля")
    public void createUserWithoutPasswordTest() {
        user = new User("test@example.com", null, "NoPasswordUser");
        Response response = userClient.createUser(user);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка ошибки при отсутствии имени")
    public void createUserWithoutNameTest() {
        user = new User("test@example.com", "password123", null);
        Response response = userClient.createUser(user);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}