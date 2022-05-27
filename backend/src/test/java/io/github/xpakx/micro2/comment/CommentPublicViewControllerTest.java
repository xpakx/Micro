package io.github.xpakx.micro2.comment;

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
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentPublicViewControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;
    private Long postId;
    private Long commentId;
    private Long maxCommentId;

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtils jwtTokenUtil;
    @Autowired
    UserService userService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        UserAccount user = new UserAccount();
        user.setUsername("user1");
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        user = userRepository.save(user);
        this.userId = user.getId();
        this.postId = addPost(userId, "post1");
        Long post2Id = addPost(userId, "post2");
        this.commentId = addComment(userId, postId, "comment1");
        addComment(userId, postId, "comment2");
        addComment(userId, postId, "comment3");
        this.maxCommentId = addComment(userId, post2Id, "comment4");
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Long addPost(Long userId, String content) {
        Post post = new Post();
        post.setContent(content);
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(userId));
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post).getId();
    }

    private Long addComment(Long userId, Long postId, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setLikeCount(0);
        comment.setDislikeCount(0);
        comment.setUser(userRepository.getById(userId));
        comment.setPost(postRepository.findById(postId).get());
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment).getId();
    }

    @Test
    void shouldReturnComment() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/comments/{postId}", commentId)
        .then()
                .statusCode(OK.value())
                .body("content", is("comment1"));
    }

    @Test
    void shouldRespondWith404IfCommentDoesNotExist() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/comments/{commentId}", maxCommentId+1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldReturnAllCommentsByPost() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/posts/{postId}/comments", postId)
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(3))
                .body("content", not(hasItem(hasProperty("content", equalTo("comment4")))));
    }
}
