package io.github.xpakx.micro2.follows;

import io.github.xpakx.micro2.follows.dto.FollowRequest;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.security.JwtTokenUtils;
import io.github.xpakx.micro2.tag.Tag;
import io.github.xpakx.micro2.tag.TagRepository;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.UserService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FollowControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtils jwtTokenUtil;
    @Autowired
    UserService userService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    FollowsRepository followsRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        UserAccount user = new UserAccount();
        user.setUsername("user1");
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        user = userRepository.save(user);
        this.userId = user.getId();
    }

    @AfterEach
    void tearDown() {
        followsRepository.deleteAll();
        tagRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String tokenFor(String username) {
        return jwtTokenUtil.generateToken(userService.loadUserByUsername(username));
    }

    @Test
    void shouldRespondWith401ToFollowUserRequestIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/follows/users")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToFollowUserRequestIfUserToFollowDoesNotExist() {
        FollowRequest request = getFollowRequest("user2");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/follows/users")
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private FollowRequest getFollowRequest(String name) {
        FollowRequest request = new FollowRequest();
        request.setName(name);
        return request;
    }

    @Test
    void shouldNotFollowAlreadyFollowedUser() {
        addUser("user2");
        followUser("user2");
        FollowRequest request = getFollowRequest("user2");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/follows/users")
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private void followUser(String username) {
        UserFollows follows = new UserFollows();
        follows.setUser(userRepository.findByUsername("user1").get());
        follows.setUsers(Set.of(userRepository.findByUsername(username).get()));
        followsRepository.save(follows);
    }

    private void addUser(String username) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        userRepository.save(user);
    }

    @Test
    void shouldFollowUser() {
        addUser("user2");
        FollowRequest request = getFollowRequest("user2");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/follows/users")
        .then()
                .statusCode(CREATED.value());
    }

    @Test
    void shouldRespondWith401ToFollowTagRequestIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/follows/tags")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToFollowTagRequestIfTagToFollowDoesNotExist() {
        FollowRequest request = getFollowRequest("tag");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/follows/tags")
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldNotFollowAlreadyFollowedTag() {
        addTag("tag");
        followTag("tag");
        FollowRequest request = getFollowRequest("tag");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/follows/tags")
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private void followTag(String tag) {
        UserFollows follows = new UserFollows();
        follows.setUser(userRepository.findByUsername("user1").get());
        follows.setTags(Set.of(tagRepository.findByName(tag).get()));
        followsRepository.save(follows);
    }

    private void addTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tagRepository.save(tag);
    }

    @Test
    void shouldFollowTag() {
        addTag("tag");
        FollowRequest request = getFollowRequest("tag");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/follows/tags")
        .then()
                .statusCode(CREATED.value());
    }
}