import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends RestAssuredClient {
    public static final String USER_REGISTER_ENDPOINT = "/api/auth/register";
    public static final String USER_LOGIN_ENDPOINT = "/api/auth/login";
    public static final String USER_UPDATE_DELETE_ENDPOINT = "/api/auth/user";

    @Step("Send POST request to " + USER_REGISTER_ENDPOINT + " to create a user")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseSpec())
                .and()
                .body(user)
                .when()
                .post("/auth/register")
                .then();
    }

    @Step("Send POST request to " + USER_LOGIN_ENDPOINT + " to log in as a selected user")
    public ValidatableResponse login(UserLogin userLogin) {
        return given()
                .spec(getBaseSpec())
                .and()
                .body(userLogin)
                .when()
                .post("/auth/login")
                .then();
    }

    @Step("Send DELETE request to " + USER_UPDATE_DELETE_ENDPOINT + " to delete a selected user")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .delete("/auth/user")
                .then();
    }

    @Step("Send PATCH request to " + USER_UPDATE_DELETE_ENDPOINT + " to update a selected user using authorization token")
    public ValidatableResponse updateAuthorized(String accessToken, UserUpdate userUpdate) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .and()
                .body(userUpdate)
                .when()
                .patch("/auth/user")
                .then();
    }

    @Step("Send PATCH request to " + USER_UPDATE_DELETE_ENDPOINT + " to update a selected user without authorization token")
    public ValidatableResponse updateUnauthorized(UserUpdate userUpdate) {
        return given()
                .spec(getBaseSpec())
                .and()
                .body(userUpdate)
                .when()
                .patch("/auth/user")
                .then();
    }
}