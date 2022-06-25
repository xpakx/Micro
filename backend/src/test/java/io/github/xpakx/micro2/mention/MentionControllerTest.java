package io.github.xpakx.micro2.mention;

import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.mention.dto.MentionReadRequest;
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
}