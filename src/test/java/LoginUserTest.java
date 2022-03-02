import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {

    public UserClient userClient;
    private String accessToken;
    private User user;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
        accessToken = userClient.create(user).extract().path("accessToken").toString().substring(7);
    }

    @After
    public void tearDown() {
        if(accessToken != null)
            userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Check that a new user can be logged in successfully using all required fields")
    @Description(UserClient.USER_LOGIN_ENDPOINT + " endpoint returns 200 status code and body 'success': 'true'")
    public void testUserIsLoggedInUsingRequiredFields() {
        ValidatableResponse response = userClient.login(UserLogin.getUserLogin(user));
        response
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Check that a new user can not be logged in using incorrect email")
    @Description(UserClient.USER_LOGIN_ENDPOINT + " endpoint returns 401 status code and body 'message': 'email or password are incorrect'")
    public void testUserIsNotLoggedInUsingIncorrectEmail() {
        Faker faker = new Faker();
        ValidatableResponse response = userClient.login(new UserLogin(faker.internet().emailAddress(), user.password));
        response
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Check that a new user can not be logged in using incorrect password")
    @Description(UserClient.USER_LOGIN_ENDPOINT + " endpoint returns 401 status code and body 'message': 'email or password are incorrect'")
    public void testUserIsNotLoggedInUsingIncorrectPassword() {
        Faker faker = new Faker();
        ValidatableResponse response = userClient.login(new UserLogin(user.email, faker.internet().password()));
        response
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
}