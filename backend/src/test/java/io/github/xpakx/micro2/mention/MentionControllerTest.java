package io.github.xpakx.micro2.mention;

import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.mention.dto.MentionReadRequest;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.security.JwtTokenUtils;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.UserService;
import io.restassured.http.ContentType;
import org.assertj.core.util.Streams;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MentionControllerTest {
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
    MentionRepository mentionRepository;
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
        mentionRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String tokenFor(String username) {
        return jwtTokenUtil.generateToken(userService.loadUserByUsername(username));
    }

    @Test
    void shouldRespondWith401ToMentionCountRequestIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/mentions/count")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith0ForNoMentions() {
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/mentions/count")
        .then()
                .statusCode(OK.value())
                .body("count", equalTo(0));
    }

    @Test
    void shouldRespondWithMentionCount() {
        addPostWithMention("@user1: post1");
        addPostWithMention("@user1: post2");
        addPostWithMention("@user1: post3");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/mentions/count")
        .then()
                .statusCode(OK.value())
                .body("count", equalTo(3));
    }

    private Long addPostWithMention(String content) {
        UserAccount user = userRepository.findByUsername("user1").get();
        Post post = new Post();
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setContent(content);
        post.setUser(user);
        post = postRepository.save(post);
        Mention mention = new Mention();
        mention.setRead(false);
        mention.setPost(post);
        mention.setAuthor(user);
        mention.setMentioned(user);
        mentionRepository.save(mention);
        return mention.getId();
    }

    @Test
    void shouldRespondWith401ToMarkMentionAsReadRequestIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/mentions/{mentionId}/read", 1L)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToMarkMentionAsReadRequestIfMentionDoesNotExist() {
        MentionReadRequest request = getMentionReadRequest();
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/mentions/{mentionId}/read", 1L)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private MentionReadRequest getMentionReadRequest() {
        MentionReadRequest request = new MentionReadRequest();
        request.setRead(true);
        return request;
    }

    @Test
    void shouldMarkMentionAsRead() {
        Long mentionId = addPostWithMention("@user1: post1");
        MentionReadRequest request = getMentionReadRequest();
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/mentions/{mentionId}/read", mentionId)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldRespondWith401ToMarkAllMentionsAsReadRequestIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/mentions/read")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldMarkAllMentionsAsRead() {
        addPostWithMention("@user1: post1");
        addPostWithMention("@user1: post2");
        addPostWithMention("@user1: post3");
        MentionReadRequest request = getMentionReadRequest();
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/mentions/read")
        .then()
                .statusCode(OK.value());
        List<Mention> mentions = Streams.stream(mentionRepository.findAll()).collect(Collectors.toList());
        assertThat(mentions, hasSize(3));
        assertThat(mentions, everyItem(hasProperty("read", equalTo(true))));
    }

    @Test
    void shouldRespondWith401ToGetMentionsRequestIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/mentions")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWithMentions() {
        addUser("user2");
        Long post1Id = addPostAndReturnId("post1", "user2");
        Long post2Id = addPostAndReturnId("post2", "user2");
        Long commentId = addCommentAndReturnId("post2", "user2", post1Id);
        addMentionForPost(post1Id);
        addMentionForPost(post2Id);
        addMentionForComment(commentId, post1Id);
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/mentions")
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(3));
    }

    private void addUser(String username) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        userRepository.save(user);
    }

    private Long addPostAndReturnId(String content, String username) {
        Post post = new Post();
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setContent(content);
        post.setUser(userRepository.findByUsername(username).get());
        return postRepository.save(post).getId();
    }

    private Long addCommentAndReturnId(String content, String username, Long postId) {
        Comment comment = new Comment();
        comment.setLikeCount(0);
        comment.setDislikeCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setContent(content);
        comment.setUser(userRepository.findByUsername(username).get());
        comment.setPost(postRepository.findById(postId).get());
        return commentRepository.save(comment).getId();
    }

    private void addMentionForPost(Long postId) {
        UserAccount user = userRepository.findByUsername("user1").get();
        Mention mention = new Mention();
        mention.setRead(false);
        mention.setPost(postRepository.findById(postId).get());
        mention.setAuthor(user);
        mention.setMentioned(user);
        mentionRepository.save(mention);
    }

    private void addMentionForComment(Long commentId, Long postId) {
        UserAccount user = userRepository.findByUsername("user1").get();
        Mention mention = new Mention();
        mention.setRead(false);
        mention.setPost(postRepository.findById(postId).get());
        mention.setComment(commentRepository.findById(commentId).get());
        mention.setAuthor(user);
        mention.setMentioned(user);
        mentionRepository.save(mention);
    }
}