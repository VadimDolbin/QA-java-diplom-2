import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTest {

    public UserClient userClient;
    public OrderClient orderClient;
    private String accessToken;
    private String[] ingredients;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        ingredients = new String[]{orderClient.getIngredients().extract().path("data[0]._id")};
        User user = User.getRandom();
        accessToken = userClient.create(user).extract().path("accessToken").toString().substring(7);
    }

    @After
    public void tearDown() {
        if(accessToken != null)
            userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Check that a new order can be created using ingredients and access token")
    @Description(OrderClient.ORDER_CREATE_GET_ENDPOINT + " endpoint returns 200 status code and body 'success': 'true'")
    public void testOrderIsCreatedUsingIngredientsAndAccessToken() {
        ValidatableResponse response = orderClient.createAuthorized(accessToken, new Order(ingredients));
        response
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Check that a new order can be created using ingredients but without access token")
    @Description(OrderClient.ORDER_CREATE_GET_ENDPOINT + " endpoint returns 200 status code and body 'success': 'true'")
    public void testOrderIsCreatedUsingIngredientsWithoutAccessToken() {
        ValidatableResponse response = orderClient.createUnauthorized(new Order(ingredients));
        response
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Check that a new order can not be created without ingredients using access token")
    @Description(OrderClient.ORDER_CREATE_GET_ENDPOINT + " endpoint returns 400 status code and body 'message': 'Ingredient ids must be provided'")
    public void testOrderIsNotCreatedWithoutIngredientsUsingAccessToken() {
        ValidatableResponse response = orderClient.createAuthorized(accessToken, new Order(null));
        response
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Check that a new order can not be created without ingredients and access token")
    @Description(OrderClient.ORDER_CREATE_GET_ENDPOINT + " endpoint returns 400 status code and body 'message': 'Ingredient ids must be provided'")
    public void testOrderIsNotCreatedWithoutIngredientsAndAccessToken() {
        ValidatableResponse response = orderClient.createUnauthorized(new Order(null));
        response
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Check that a new order can not be created using invalid ingredients and access token")
    @Description(OrderClient.ORDER_CREATE_GET_ENDPOINT + " endpoint returns 500 status code")
    public void testOrderIsNotCreatedUsingInvalidIngredientsAndAccessToken() {
        Faker faker = new Faker();
        ValidatableResponse response = orderClient.createAuthorized(accessToken, new Order(new String[]{faker.internet().macAddress()}));
        response
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Check that a new order can not be created using invalid ingredients and without access token")
    @Description(OrderClient.ORDER_CREATE_GET_ENDPOINT + " endpoint returns 500 status code")
    public void testOrderIsNotCreatedUsingInvalidIngredientsWithoutAccessToken() {
        Faker faker = new Faker();
        ValidatableResponse response = orderClient.createUnauthorized(new Order(new String[]{faker.internet().macAddress()}));
        response
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}