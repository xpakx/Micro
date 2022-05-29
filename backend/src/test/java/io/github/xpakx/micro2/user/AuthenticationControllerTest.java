package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.security.JwtTokenUtils;
import io.github.xpakx.micro2.user.dto.AuthenticationRequest;
import io.github.xpakx.micro2.user.dto.RegistrationRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtils jwtTokenUtil;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        UserAccount user = new UserAccount();
        user.setUsername("user1");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(new HashSet<>());
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    private String tokenFor(String username) {
        return jwtTokenUtil.generateToken(userService.loadUserByUsername(username));
    }

    @Test
    void shouldAuthenticate() {
        String token = given()
                .log()
                .uri()
                .contentType(ContentType.JSON)
                .body(getAuthRequest("user1", "password"))
        .when()
                .post(baseUrl + "/authenticate")
                .then()
                .statusCode(OK.value())
                .body("$", hasKey("token"))
        .extract()
                .jsonPath()
                .getString("token");

        given()
                .auth()
                .oauth2(token)
        .when()
                .post(baseUrl + "/posts")
        .then()
                .statusCode(not(UNAUTHORIZED.value()));
    }

    private AuthenticationRequest getAuthRequest(String username, String password) {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    @Test
    void shouldNotAuthenticateIfBadPassword() {
        given()
                .log()
                .uri()
                .contentType(ContentType.JSON)
                .body(getAuthRequest("user1", "badPassword"))
        .when()
                .post(baseUrl + "/authenticate")
        .then()
                .statusCode(UNAUTHORIZED.value())
                .body("$", not(hasKey("token")));
    }

    @Test
    void shouldNotAuthenticateIfUserNotFound() {
        given()
                .log()
                .uri()
                .contentType(ContentType.JSON)
                .body(getAuthRequest("notUser", "password"))
        .when()
                .post(baseUrl + "/authenticate")
        .then()
                .statusCode(UNAUTHORIZED.value())
                .body("$", not(hasKey("token")));
    }

    @Test
    void shouldRegisterUser() {
        String token = given()
                .log()
                .uri()
                .contentType(ContentType.JSON)
                .body(getRegistrationRequest("user2", "password", "password"))
        .when()
                .post(baseUrl + "/register")
        .then()
                .statusCode(CREATED.value())
                .body("$", hasKey("token"))
                .extract()
                .jsonPath()
                .getString("token");

        given()
                .auth()
                .oauth2(token)
        .when()
                .post(baseUrl + "/posts")
        .then()
                .statusCode(not(UNAUTHORIZED.value()));
    }

    private RegistrationRequest getRegistrationRequest(String username, String password, String passwordRepeated) {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setPasswordRe(passwordRepeated);
        return request;
    }

    @Test
    void shouldNotRegisterIfPasswordsDoNotMatch() {
        given()
                .log()
                .uri()
                .contentType(ContentType.JSON)
                .body(getRegistrationRequest("user2", "password1", "password2"))
        .when()
                .post(baseUrl + "/register")
        .then()
                .log()
                .body()
                .statusCode(BAD_REQUEST.value())
                .body("$", not(hasKey("token")))
                .body("message", containsString("don't match"));
    }

    @Test
    void shouldNotRegisterIfUsernameAlreadyUsed() {
        given()
                .log()
                .uri()
                .contentType(ContentType.JSON)
                .body(getRegistrationRequest("user1", "password", "password"))
        .when()
                .post(baseUrl + "/register")
        .then()
                .log()
                .body()
                .statusCode(BAD_REQUEST.value())
                .body("$", not(hasKey("token")))
                .body("message", containsString("exists"));
    }
}