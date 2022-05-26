package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostRequest;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerTest {
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
        postRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String tokenFor(String username) {
        return jwtTokenUtil.generateToken(userService.loadUserByUsername(username));
    }

    @Test
    void shouldRespondWith401ToAddPostIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/user/{username}/post", "user1")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldAddNewPost() {
        PostRequest request = getValidPostRequest("content");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/user/{username}/post", "user1")
        .then()
                .statusCode(CREATED.value())
                .body("message", equalTo(request.getMessage()))
                .body("username", equalTo("user1"))
                .body("likeCount", equalTo(0))
                .body("dislikeCount", equalTo(0));
    }

    private PostRequest getValidPostRequest(String content) {
        PostRequest request = new PostRequest();
        request.setMessage(content);
        return request;
    }

    @Test
    void shouldAddNewPostWithTag() {
        PostRequest request = getValidPostRequest("content #tag");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/user/{username}/post", "user1")
        .then()
                .statusCode(CREATED.value())
                .body("message", equalTo(request.getMessage()));

        Optional<Tag> tagInDb = tagRepository.findByName("tag");
        assertTrue(tagInDb.isPresent());
    }

    @Test
    void shouldRespondWith401ToUpdatePostIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .put(baseUrl + "/user/{username}/post/{postId}", "user1", 1)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotUpdatePostByOtherUser() {
        Long id = addPostAndReturnId();
        createUser("user2");
        PostRequest request = getValidPostRequest("post");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user2"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/user/{username}/post/{postId}", "user2", id)
        .then()
                .statusCode(NOT_FOUND.value());
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

    private void createUser(String username) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        userRepository.save(user);
    }

    @Test
    void shouldRespondWith404WhileUpdatingNonExistentPost() {
        PostRequest request = getValidPostRequest("post");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/user/{username}/post/{postId}", "user1", 1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldNotUpdatePostOlderThan24Hours() {
        Long id = addOutdatedPostAndReturnId();
        PostRequest request = getValidPostRequest("post");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/user/{username}/post/{postId}", "user1", id)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private Long addOutdatedPostAndReturnId() {
        Post post = new Post();
        post.setContent("content");
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(userId));
        post.setCreatedAt(LocalDateTime.now().minusDays(2));
        return postRepository.save(post).getId();
    }
    @Test
    void shouldUpdatePost() {
        Long id = addPostAndReturnId();
        PostRequest request = getValidPostRequest("updated");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/user/{username}/post/{postId}", "user1", id)
        .then()
                .statusCode(OK.value())
                .body("message", is("updated"));
    }

    @Test
    void shouldAddTagWhileUpdatingPost() {
        Long id = addPostAndReturnId();
        PostRequest request = getValidPostRequest("updated #tag");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/user/{username}/post/{postId}", "user1", id)
        .then()
                .statusCode(OK.value());

        Optional<Tag> tagInDb = tagRepository.findByName("tag");
        assertTrue(tagInDb.isPresent());
    }
}
