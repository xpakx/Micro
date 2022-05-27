package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.security.JwtTokenUtils;
import io.github.xpakx.micro2.tag.Tag;
import io.github.xpakx.micro2.tag.TagRepository;
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
import java.util.List;
import java.util.stream.Collectors;

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
    private Long postId;
    private Long maxPostId;

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
        Long userId = user.getId();
        UserAccount user2 = new UserAccount();
        user2.setUsername("user2");
        user2.setPassword("password");
        user2.setRoles(new HashSet<>());
        user2 = userRepository.save(user2);
        Long user2Id = user2.getId();
        Long tag1 = addTagAndReturnId("tag1");
        Long tag2 = addTagAndReturnId("tag2");
        this.postId = addPost(user2Id, "post1", List.of());
        addPost(user2Id, "post2", List.of(tag1));
        addPost(userId, "post3", List.of(tag1));
        addPost(userId, "post4", List.of(tag2));
        this.maxPostId = addPost(userId, "post5", List.of(tag2, tag1));
    }

    private Long addTagAndReturnId(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag).getId();
    }

    private Long addPost(Long userId, String content, List<Long> tags) {
        Post post = new Post();
        post.setContent(content);
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(userId));
        post.setCreatedAt(LocalDateTime.now());
        post.setTags(tags.stream().map((i) -> tagRepository.getById(i)).collect(Collectors.toSet()));
        return postRepository.save(post).getId();
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
                .body("content", not(hasItem(hasProperty("content", equalTo("post1")))))
                .body("content", not(hasItem(hasProperty("content", equalTo("post2")))));
    }

    @Test
    void shouldReturnAllPostsByTag() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/tags/{tag}/posts", "tag1")
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(3))
                .body("content", not(hasItem(hasProperty("content", equalTo("post1")))))
                .body("content", not(hasItem(hasProperty("content", equalTo("post4")))));
    }

    @Test
    void shouldReturnPost() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/post/{postId}/min", postId)
        .then()
                .statusCode(OK.value())
                .body("content", is("post1"));
    }

    @Test
    void shouldRespondWith404IfPostDoesNotExist() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/post/{postId}/min", maxPostId+1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldReturnPostWithComments() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/post/{postId}", postId)
        .then()
                .statusCode(OK.value())
                .body("post.content", is("post1"));
    }

    @Test
    void shouldRespondWith404IfPostWithCommentsDoesNotExist() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/post/{postId}", maxPostId+1)
        .then()
                .statusCode(NOT_FOUND.value());
    }
}
