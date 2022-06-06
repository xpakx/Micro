package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentRequest;
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
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {
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
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String tokenFor(String username) {
        return jwtTokenUtil.generateToken(userService.loadUserByUsername(username));
    }

    @Test
    void shouldRespondWith401ToAddCommentIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/posts/{postId}/comments", "user1", 1)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotAddNewCommentToNonexistentPost() {
        CommentRequest request = getValidCommentRequest("content");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/posts/{postId}/comments", "user1", 1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldAddNewComment() {
        CommentRequest request = getValidCommentRequest("content");
        Long postId = addPostAndReturnId();
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/posts/{postId}/comments", "user1", postId)
        .then()
                .statusCode(CREATED.value())
                .body("message", equalTo(request.getMessage()))
                .body("username", equalTo("user1"))
                .body("likeCount", equalTo(0))
                .body("dislikeCount", equalTo(0));
    }

    private CommentRequest getValidCommentRequest(String content) {
        CommentRequest request = new CommentRequest();
        request.setMessage(content);
        return request;
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
    void shouldRespondWith401ToDeleteCommentIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .delete(baseUrl + "/comments/{commentId}", "user1", 1)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotDeleteCommentByOtherUser() {
        Long id = addCommentAndReturnId(addPostAndReturnId());
        createUser("user2");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user2"))
        .when()
                .delete(baseUrl + "/comments/{commentId}", "user2", id)
        .then()
                .statusCode(FORBIDDEN.value());
    }

    private Long addCommentAndReturnId(Long postId) {
        Comment comment = new Comment();
        comment.setContent("content");
        comment.setLikeCount(0);
        comment.setDislikeCount(0);
        comment.setUser(userRepository.getById(userId));
        comment.setPost(postRepository.findById(postId).get());
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment).getId();
    }

    private void createUser(String username) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        userRepository.save(user);
    }

    @Test
    void shouldRespondWith404WhileDeletingNonExistentComment() {
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
        .when()
                .delete(baseUrl + "/comments/{commentId}", "user1", 1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldDeleteComment() {
        Long id = addCommentAndReturnId(addPostAndReturnId());
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
        .when()
                .delete(baseUrl + "/comments/{commentId}", "user1", id)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldRespondWith401ToUpdateCommentIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .put(baseUrl + "/comments/{commentId}", "user1", 1)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotUpdateCommentByOtherUser() {
        Long id = addCommentAndReturnId(addPostAndReturnId());
        createUser("user2");
        CommentRequest request = getValidCommentRequest("post");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user2"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/comments/{commentId}", "user2", id)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldRespondWith404WhileUpdatingNonExistentComment() {
        CommentRequest request = getValidCommentRequest("post");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/comments/{commentId}", "user1", 1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldNotUpdateCommentOlderThan24Hours() {
        Long id = addOutdatedCommentAndReturnId(addPostAndReturnId());
        CommentRequest request = getValidCommentRequest("post");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/comments/{commentId}", "user1", id)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private Long addOutdatedCommentAndReturnId(Long postId) {
        Comment comment = new Comment();
        comment.setContent("content");
        comment.setLikeCount(0);
        comment.setDislikeCount(0);
        comment.setUser(userRepository.getById(userId));
        comment.setPost(postRepository.findById(postId).get());
        comment.setCreatedAt(LocalDateTime.now().minusDays(2));
        return commentRepository.save(comment).getId();
    }

    @Test
    void shouldNotUpdateCommentWithResponses() {
        Long postId = addPostAndReturnId();
        Long id = addHourOldCommentAndReturnId(postId);
        addCommentAndReturnId(postId);
        CommentRequest request = getValidCommentRequest("post");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/comments/{commentId}", "user1", id)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private Long addHourOldCommentAndReturnId(Long postId) {
        Comment comment = new Comment();
        comment.setContent("content");
        comment.setLikeCount(0);
        comment.setDislikeCount(0);
        comment.setUser(userRepository.getById(userId));
        comment.setPost(postRepository.findById(postId).get());
        comment.setCreatedAt(LocalDateTime.now().minusHours(1));
        return commentRepository.save(comment).getId();
    }

    @Test
    void shouldUpdateComment() {
        Long id = addCommentAndReturnId(addPostAndReturnId());
        CommentRequest request = getValidCommentRequest("updated");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/comments/{commentId}", "user1", id)
        .then()
                .statusCode(OK.value())
                .body("message", is("updated"));
    }
}
