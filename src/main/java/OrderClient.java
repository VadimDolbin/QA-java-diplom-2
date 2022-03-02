import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient{

    public static final String ORDER_CREATE_GET_ENDPOINT = "/api/orders";
    public static final String ORDER_INGREDIENTS_ENDPOINT = "/api/ingredients";

    @Step("Send GET request to " + ORDER_INGREDIENTS_ENDPOINT + " to get the list of available ingredients")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get("/ingredients")
                .then();
    }

    @Step("Send POST request to " + ORDER_CREATE_GET_ENDPOINT + " to create an order using authorization token")
    public ValidatableResponse createAuthorized(String accessToken, Order order) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .and()
                .body(order)
                .when()
                .post("/orders")
                .then();
    }

    @Step("Send POST request to " + ORDER_CREATE_GET_ENDPOINT + " to create an order without authorization token")
    public ValidatableResponse createUnauthorized(Order order) {
        return given()
                .spec(getBaseSpec())
                .and()
                .body(order)
                .when()
                .post("/orders")
                .then();
    }

    @Step("Send GET request to " + ORDER_CREATE_GET_ENDPOINT + " to get user orders using authorization token")
    public ValidatableResponse getAuthorized(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .get("/orders")
                .then();
    }

    @Step("Send GET request to " + ORDER_CREATE_GET_ENDPOINT + " to get user orders without authorization token")
    public ValidatableResponse getUnauthorized() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get("/orders")
                .then();
    }
}