
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
@Epic("PetStoreTest")
@Feature("Check work with user data")
public class PetStoreTest {

    String TEST_URL = "https://petstore.swagger.io/v2";

    UserGenerator userGenerator = new UserGenerator();
    public String generateUserName() {
        return userGenerator.createStringFromSymbols(10);
    }

    public String generateEmail() {
        return userGenerator.createStringFromSymbols(10) + "@" + userGenerator.createStringFromSymbols(6) + ".com";
    }
    @Test
    @Story("We try to create user, check it, update data and delete after that")
    @Description("User test with updated data")
    public void checkUser() {
        RestAssured.baseURI = TEST_URL;
        Integer id = Integer.valueOf(userGenerator.createStringFromNumbers(4));
        String userName = generateUserName();
        String firstName = userGenerator.createStringFromLetters(6);
        String lastName = userGenerator.createStringFromLetters(10);
        String email = generateEmail();
        String password = userGenerator.createStringFromSymbols(15);
        String phone = userGenerator.createStringFromNumbers(10);
        Integer userStatus = 0;
        UserData user = new UserData(id, userName, firstName, lastName, email, password, phone, userStatus);

        // Create new user
        Response response = given()
                .header("Content-type", "application/json")
                .contentType(ContentType.JSON)
                .and()
                .body(convertUserToJsonString(user))
                .when()
                .post("/user")
                .then()
                .extract().response();

        // check response is ok and contains id
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(user.id.toString(), response.jsonPath().get("message"));

        // get user and compare data
        response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/user/" + user.username)
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());
        checkUserWithResponse(user, response);

        // update user
        user.phone = "123456789";
        user.firstName = "Anton";
        user.lastName = "Usanov";
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(convertUserToJsonString(user))
                .when()
                .put("/user/" + user.username)
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());

        // get user and compare data
        response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/user/" + user.username)
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());
        checkUserWithResponse(user, response);

        response = given()
                .header("Content-type", "application/json")
                .when()
                .delete("/user/" + user.username)
                .then()
                .extract().response();
        Assertions.assertEquals(200, response.statusCode());

        // get user and check it's not found
        response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/user/" + user.username)
                .then()
                .extract().response();
        Assertions.assertEquals(404, response.statusCode());
    }

    void checkUserWithResponse(UserData user, Response response) {
        Assertions.assertAll("Test received user with expected data",
                () -> Assertions.assertEquals(response.jsonPath().get("id"), user.id),
                () -> Assertions.assertEquals(response.jsonPath().get("username"), user.username),
                () -> Assertions.assertEquals(response.jsonPath().get("firstName"), user.firstName),
                () -> Assertions.assertEquals(response.jsonPath().get("lastName"), user.lastName),
                () -> Assertions.assertEquals(response.jsonPath().get("email"), user.email),
                () -> Assertions.assertEquals(response.jsonPath().get("password"), user.password),
                () -> Assertions.assertEquals(response.jsonPath().get("phone"), user.phone),
                () -> Assertions.assertEquals(response.jsonPath().get("userStatus"), user.userStatus));
    }

    String convertUserToJsonString(UserData user) {
        JSONObject userObject = new JSONObject()
                .put("id", user.id)
                .put("username", user.username)
                .put("firstName", user.firstName)
                .put("lastName", user.lastName)
                .put("email", user.email)
                .put("password", user.password)
                .put("phone", user.phone)
                .put("userStatus", user.userStatus);
        return userObject.toString();
    }
}
