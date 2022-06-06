package io.github.xpakx.micro2.like;

import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.comment.CommentRepository;
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
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentLikeControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;
    private Long postId;

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtils jwtTokenUtil;
    @Autowired
    UserService userService;
    @Autowired
    CommentRepository commentRepository;
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
        Post post = new Post();
        post.setContent("content");
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(userId));
        post.setCreatedAt(LocalDateTime.now());
        this.postId = postRepository.save(post).getId();
    }

    @AfterEach
    void tearDown() {
        likeRepository.deleteAll();
        commentRepository.deleteAll();
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
                .post(baseUrl + "/comments/{commentId}/like", "user1", 1)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotLikeNonexistentComment() {
        LikeRequest request = getLikeRequest(true);
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/comments/{commentId}/like", "user1", 1)
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
        Long commentId = addCommentAndReturnId();
        LikeRequest request = getLikeRequest(true);
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/comments/{commentId}/like", "user1", commentId)
        .then()
                .statusCode(CREATED.value())
                .body("commentId", equalTo(Math.toIntExact(commentId)))
                .body("voted", equalTo(true))
                .body("positive", equalTo(true))
                .body("totalLikes", equalTo(1))
                .body("totalDislikes", equalTo(0));
    }

    private Long addCommentAndReturnId() {
        Comment comment = new Comment();
        comment.setContent("content");
        comment.setLikeCount(0);
        comment.setDislikeCount(0);
        comment.setUser(userRepository.getById(userId));
        comment.setPost(postRepository.findById(postId).get());
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment).getId();
    }

    @Test
    void shouldCreateNewDislike() {
        Long commentId = addCommentAndReturnId();
        LikeRequest request = getLikeRequest(false);
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/comments/{commentId}/like", "user1", commentId)
        .then()
                .statusCode(CREATED.value())
                .body("commentId", equalTo(Math.toIntExact(commentId)))
                .body("voted", equalTo(true))
                .body("positive", equalTo(false))
                .body("totalLikes", equalTo(0))
                .body("totalDislikes", equalTo(1));
    }

    @Test
    void shouldSwitchLike() {
        Long commentId = addLikedCommentAndReturnId();
        LikeRequest request = getLikeRequest(false);
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/comments/{commentId}/like", "user1", commentId)
        .then()
                .statusCode(CREATED.value())
                .body("commentId", equalTo(Math.toIntExact(commentId)))
                .body("voted", equalTo(true))
                .body("positive", equalTo(false))
                .body("totalLikes", equalTo(0))
                .body("totalDislikes", equalTo(1));
    }

    private Long addLikedCommentAndReturnId() {
        Comment comment = new Comment();
        comment.setContent("content");
        comment.setLikeCount(1);
        comment.setDislikeCount(0);
        comment.setUser(userRepository.getById(userId));
        comment.setPost(postRepository.findById(postId).get());
        comment.setCreatedAt(LocalDateTime.now());
        Long commentId = commentRepository.save(comment).getId();
        Like like = new Like();
        like.setPositive(true);
        like.setComment(commentRepository.findById(commentId).get());
        like.setUser(userRepository.getById(userId));
        likeRepository.save(like);
        return commentId;
    }

    @Test
    void shouldRespondWith401ToUnLikeIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .delete(baseUrl + "/comments/{commentId}/like", "user1", 1)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotUnlikeIfLikeDoesNotExist() {
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
        .when()
                .delete(baseUrl + "/comments/{commentId}/like", "user1", 1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldUnlikeComment() {
        Long commentId = addLikedCommentAndReturnId();
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
        .when()
                .delete(baseUrl + "/comments/{commentId}/like", "user1", commentId)
        .then()
                .statusCode(OK.value())
                .body("totalLikes", equalTo(0))
                .body("totalDislikes", equalTo(0));
    }

    @Test
    void shouldRespondWith404IfLikeDoesNotExist() {
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/comments/{commentId}/like", "user1", 1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldGetLikeForComment() {
        Long commentId = addLikedCommentAndReturnId();
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/comments/{commentId}/like", "user1", commentId)
        .then()
                .statusCode(OK.value())
                .body("positive", is(true));
    }
}
