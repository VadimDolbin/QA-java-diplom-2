import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

    public UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if(accessToken != null)
            userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Check that a new user can be created using all required fields")
    @Description(UserClient.USER_REGISTER_ENDPOINT + " endpoint returns 200 status code and body 'success': 'true'")
    public void testUserIsCreatedUsingRequiredFields() {
        User user = User.getRandom();
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken").toString().substring(7);

        response
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Check that a duplicated user can not be created")
    @Description(UserClient.USER_REGISTER_ENDPOINT + " endpoint returns 403 status code and body 'message': 'User already exists'")
    public void testDuplicatedUserIsNotCreated() {
        User user = User.getRandom();
        accessToken = userClient.create(user).extract().path("accessToken").toString().substring(7);
        ValidatableResponse response = userClient.create(user);

        response
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Check that a new user can not be created without an email")
    @Description(UserClient.USER_REGISTER_ENDPOINT + " endpoint returns 403 status code and body 'message': 'Email, password and name are required fields'")
    public void testUserIsNotCreatedWithoutEmail() {
        User user = User.getRandomWithoutEmail();
        ValidatableResponse response = userClient.create(user);

        response
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check that a new user can not be created without a password")
    @Description(UserClient.USER_REGISTER_ENDPOINT + " endpoint returns 403 status code and body 'message': 'Email, password and name are required fields'")
    public void testUserIsNotCreatedWithoutPassword() {
        User user = User.getRandomWithoutPassword();
        ValidatableResponse response = userClient.create(user);

        response
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check that a new user can not be created without a name")
    @Description(UserClient.USER_REGISTER_ENDPOINT + " endpoint returns 403 status code and body 'message': 'Email, password and name are required fields'")
    public void testUserIsNotCreatedWithoutName() {
        User user = User.getRandomWithoutName();
        ValidatableResponse response = userClient.create(user);

        response
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}