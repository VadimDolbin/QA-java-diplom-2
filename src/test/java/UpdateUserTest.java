import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest {

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
    @DisplayName("Check that a new user can be updated successfully using access token")
    @Description(UserClient.USER_UPDATE_DELETE_ENDPOINT + " endpoint returns 200 status code and body containing updated user 'email' and 'name' fields")
    public void testUserIsUpdatedUsingAccessToken() {
        Faker faker = new Faker();
        UserUpdate userUpdate = new UserUpdate(faker.internet().emailAddress(), faker.name().fullName());
        ValidatableResponse response = userClient.updateAuthorized(accessToken, userUpdate);

        response
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("user.email", equalTo(userUpdate.email))
                .and()
                .body("user.name", equalTo(userUpdate.name));
    }

    @Test
    @DisplayName("Check that a new user can not be updated without access token")
    @Description(UserClient.USER_UPDATE_DELETE_ENDPOINT + " endpoint returns 401 status code and body 'message': 'You should be authorised'")
    public void testUserIsNotUpdatedWithoutAccessToken() {
        Faker faker = new Faker();
        UserUpdate userUpdate = new UserUpdate(faker.internet().emailAddress(), faker.name().fullName());
        ValidatableResponse response = userClient.updateUnauthorized(userUpdate);

        response
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Check that a new user can not be updated using already existing email")
    @Description(UserClient.USER_UPDATE_DELETE_ENDPOINT + " endpoint returns 403 status code and body 'message': 'User with such email already exists'")
    public void testUserIsNotUpdatedUsingExistingEmail() {
        UserUpdate userUpdate = UserUpdate.getUserUpdate(user);
        User userChanged = User.getRandom();
        String accessTokenChanged = userClient.create(userChanged).extract().path("accessToken").toString().substring(7);
        ValidatableResponse response = userClient.updateAuthorized(accessTokenChanged, userUpdate);

        response
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User with such email already exists"));

        userClient.delete(accessTokenChanged);
    }
}