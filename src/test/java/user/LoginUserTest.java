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
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(accessToken);
        }
    }

    @Step("Вход пользователя: {user.email}")
    private void login(User user) {
        Response response = userClient.loginUser(user);
        response.then().statusCode(SC_OK);
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    @Description("Проверка успешного входа с корректными данными")
    public void loginExistingUserTest() {
        Response response = userClient.loginUser(user);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным логином")
    @Description("Проверка ошибки при вводе неправильного email")
    public void loginWithInvalidEmailTest() {
        User invalidEmailUser = new User("wrong_" + System.currentTimeMillis() + "@example.com", "password123", null);
        Response response = userClient.loginUser(invalidEmailUser);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    @Description("Проверка ошибки при вводе неправильного пароля")
    public void loginWithInvalidPasswordTest() {
        User invalidPasswordUser = new User(user.getEmail(), "wrongpassword", null);
        Response response = userClient.loginUser(invalidPasswordUser);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}