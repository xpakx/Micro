package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.security.JwtTokenUtils;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostPublicViewControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;
    private Long user2Id;

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
        UserAccount user2 = new UserAccount();
        user2.setUsername("user2");
        user2.setPassword("password");
        user2.setRoles(new HashSet<>());
        user2 = userRepository.save(user2);
        this.user2Id = user2.getId();
        addPost(user2Id, "post1");
        addPost(user2Id, "post2");
        addPost(userId, "post3");
        addPost(userId, "post4");
        addPost(userId, "post5");
    }

    private void addPost(Long userId, String content) {
        Post post = new Post();
        post.setContent(content);
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(userId));
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
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
    void shouldReturnAllPosts() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/posts")
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(5));
    }

    @Test
    void shouldReturnAllPostsByUser() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/user/{username}/posts", "user1")
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(3))
                .body("content", not(hasItem(hasProperty("content", equalTo("post1")))));
    }
}