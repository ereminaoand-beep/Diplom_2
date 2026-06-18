package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.User;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru";
    private static final String REGISTER = "/api/auth/register";
    private static final String LOGIN = "/api/auth/login";

    @Step("Создание пользователя: {user.email}")
    public Response createUser(User user) {
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + REGISTER);
    }

    @Step("Логин пользователя: {user.email}")
    public Response loginUser(User user) {
        return given()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + LOGIN);
    }

    @Step("Получение accessToken из ответа")
    public String getAccessToken(Response response) {
        return response.jsonPath().getString("accessToken");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(BASE_URL + "/api/auth/user");
    }
}