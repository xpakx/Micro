package io.github.xpakx.micro2.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserPublicControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSuggestUserCompletions() {
        createUsersForAutocompletion();
        given()
                .log() 
                .uri()
        .when()
                .get(baseUrl + "/users/name?start={name}", "use")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(2))
                .body("$", not(hasItem(hasProperty("username", equalTo("usr")))));
    }

    private void createUsersForAutocompletion() {
        addUser("usr");
        addUser("user");
        addUser("user2");
    }

    private void addUser(String username) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        userRepository.save(user);
    }
}