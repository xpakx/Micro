package io.github.xpakx.micro2.fav;

import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.security.JwtTokenUtils;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FavPostControllerTest {
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
    FavPostRepository favRepository;
    @Autowired
    PostRepository postRepository;

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
        favRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String tokenFor(String username) {
        return jwtTokenUtil.generateToken(userService.loadUserByUsername(username));
    }

    @Test
    void shouldRespondWith401ToFavPostRequestIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/posts/{postId}/fav", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToFavPostRequestIfPostDoesNotExist() {
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .post(baseUrl + "/posts/{postId}/fav", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldFavPost() {
        Long postId = addPostAndReturnId();
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .post(baseUrl + "/posts/{postId}/fav", postId)
        .then()
                .statusCode(CREATED.value());
        boolean isFavFoundInDb = favRepository.existsByPostIdAndUserUsername(postId, "user1");
        assertTrue(isFavFoundInDb);
    }

    private Long addPostAndReturnId() {
        Post post = new Post();
        post.setContent("content");
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(userId));
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post).getId();
    }

    @Test
    void shouldNotFavAlreadyFavedPost() {
        Long postId = addPostAndReturnId();
        favPost(postId, "user1");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .post(baseUrl + "/posts/{postId}/fav", postId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private void favPost(Long postId, String username) {
        FavPost fav = new FavPost();
        fav.setPost(postRepository.findById(postId).get());
        fav.setUser(userRepository.findByUsername(username).get());
        favRepository.save(fav);
    }

    @Test
    void shouldRespondWith401ToUnfavPostRequestIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .delete(baseUrl + "/posts/{postId}/fav", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldUnfavPost() {
        Long postId = addPostAndReturnId();
        favPost(postId, "user1");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .delete(baseUrl + "/posts/{postId}/fav", postId)
        .then()
                .statusCode(OK.value());
        boolean isFavFoundInDb = favRepository.existsByPostIdAndUserUsername(postId, "user1");
        assertFalse(isFavFoundInDb);
    }

    @Test
    void shouldNotUnfavNotFavPost() {
        Long postId = addPostAndReturnId();
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .delete(baseUrl + "/posts/{postId}/fav", postId)
        .then()
                .statusCode(BAD_REQUEST.value());
    }
}