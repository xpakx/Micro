package io.github.xpakx.micro2.like;

import io.github.xpakx.micro2.like.dto.LikeRequest;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.security.JwtTokenUtils;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostLikeControllerTest {
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
    LikeRepository likeRepository;

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
        likeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String tokenFor(String username) {
        return jwtTokenUtil.generateToken(userService.loadUserByUsername(username));
    }

    @Test
    void shouldRespondWith401ToAddLikeIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/user/{username}/posts/{postId}/like", "user1", 1)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotLikeNonexistentPost() {
        LikeRequest request = getLikeRequest(true);
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/user/{username}/posts/{postId}/like", "user1", 1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private LikeRequest getLikeRequest(boolean like) {
        LikeRequest request = new LikeRequest();
        request.setLike(like);
        return request;
    }

    @Test
    void shouldCreateNewLike() {
        Long postId = addPostAndReturnId();
        LikeRequest request = getLikeRequest(true);
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/user/{username}/posts/{postId}/like", "user1", postId)
        .then()
                .statusCode(CREATED.value())
                .body("postId", equalTo(Math.toIntExact(postId)))
                .body("voted", equalTo(true))
                .body("positive", equalTo(true))
                .body("totalLikes", equalTo(1))
                .body("totalDislikes", equalTo(0));
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
    void shouldCreateNewDislike() {
        Long postId = addPostAndReturnId();
        LikeRequest request = getLikeRequest(false);
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/user/{username}/posts/{postId}/like", "user1", postId)
        .then()
                .statusCode(CREATED.value())
                .body("postId", equalTo(Math.toIntExact(postId)))
                .body("voted", equalTo(true))
                .body("positive", equalTo(false))
                .body("totalLikes", equalTo(0))
                .body("totalDislikes", equalTo(1));
    }

    @Test
    void shouldSwitchLike() {
        Long postId = addLikedPostAndReturnId();
        LikeRequest request = getLikeRequest(false);
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/user/{username}/posts/{postId}/like", "user1", postId)
        .then()
                .statusCode(CREATED.value())
                .body("postId", equalTo(Math.toIntExact(postId)))
                .body("voted", equalTo(true))
                .body("positive", equalTo(false))
                .body("totalLikes", equalTo(0))
                .body("totalDislikes", equalTo(1));
    }

    private Long addLikedPostAndReturnId() {
        Post post = new Post();
        post.setContent("content");
        post.setLikeCount(1);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(userId));
        post.setCreatedAt(LocalDateTime.now());
        Long postId = postRepository.save(post).getId();
        Like like = new Like();
        like.setPositive(true);
        like.setPost(postRepository.findById(postId).get());
        like.setUser(userRepository.getById(userId));
        likeRepository.save(like);
        return postId;
    }
}