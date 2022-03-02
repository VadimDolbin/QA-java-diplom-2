import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest {

    public UserClient userClient;
    public OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        User user = User.getRandom();
        accessToken = userClient.create(user).extract().path("accessToken").toString().substring(7);
        String[] ingredients = new String[]{orderClient.getIngredients().extract().path("data[0]._id")};
        orderClient.createAuthorized(accessToken, new Order(ingredients));
    }

    @After
    public void tearDown() {
        if(accessToken != null)
            userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Check that a list of user orders is returned using access token")
    @Description(OrderClient.ORDER_CREATE_GET_ENDPOINT + " endpoint returns 200 status code and and not empty list of orders")
    public void testListOfOrdersIsReturnedUsingAccessToken() {
        ValidatableResponse response = orderClient.getAuthorized(accessToken);
        response
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("total", notNullValue());
    }

    @Test
    @DisplayName("Check that a list of user orders is not returned without access token")
    @Description(OrderClient.ORDER_CREATE_GET_ENDPOINT + " endpoint returns 401 status code and body 'message': 'You should be authorised'")
    public void testListOfOrdersIsNotReturnedWithoutAccessToken() {
        ValidatableResponse response = orderClient.getUnauthorized();
        response
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}