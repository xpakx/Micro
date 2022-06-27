package io.github.xpakx.micro2.message;

import io.github.xpakx.micro2.message.dto.MessageRequest;
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

import java.util.HashSet;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PrivateMessageControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    private Long userId;
    private Long recipientId;

    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenUtils jwtTokenUtil;
    @Autowired
    UserService userService;
    @Autowired
    PrivateMessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost".concat(":").concat(port + "");
        UserAccount user = new UserAccount();
        user.setUsername("user1");
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        user = userRepository.save(user);
        this.userId = user.getId();
        UserAccount recipient = new UserAccount();
        recipient.setUsername("user2");
        recipient.setPassword("password");
        recipient.setRoles(new HashSet<>());
        recipient = userRepository.save(recipient);
        this.recipientId = recipient.getId();
    }

    @AfterEach
    void tearDown() {
        messageRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String tokenFor(String username) {
        return jwtTokenUtil.generateToken(userService.loadUserByUsername(username));
    }

    @Test
    void shouldRespondWith401ToSendMessageRequestIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/users/{recipient}/messages", "user2")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith404ToSendMessageRequestIfRecipientDoesNotExist() {
        MessageRequest request = getMessageRequest("msg");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/users/{recipient}/messages", "user3")
        .then()
                .statusCode(NOT_FOUND.value());
    }

    private MessageRequest getMessageRequest(String content) {
        MessageRequest request = new MessageRequest();
        request.setContent(content);
        return request;
    }

    @Test
    void shouldSendMessage() {
        MessageRequest request = getMessageRequest("msg");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/users/{recipient}/messages", "user2")
        .then()
                .statusCode(CREATED.value());

        List<PrivateMessage> messagesInDb = messageRepository.findAllByRecipientUsernameAndReadIsFalse("user2");
        assertThat(messagesInDb, hasSize(1));
        assertThat(messagesInDb, hasItem(hasProperty("content", equalTo(request.getContent()))));
    }

    @Test
    void shouldRespondWith401ToMessageCountRequestIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/messages/count")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldRespondWith0ForNoMessages() {
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user2"))
        .when()
                .get(baseUrl + "/messages/count")
        .then()
                .statusCode(OK.value())
                .body("count", equalTo(0));
    }

    @Test
    void shouldRespondWithMessageCount() {
        addMessage("msg1");
        addMessage("msg2");
        addMessage("msg3");
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user2"))
        .when()
                .get(baseUrl + "/messages/count")
        .then()
                .statusCode(OK.value())
                .body("count", equalTo(3));
    }

    private void addMessage(String content) {
        PrivateMessage message = new PrivateMessage();
        message.setRead(false);
        message.setContent(content);
        message.setSender(userRepository.getById(userId));
        message.setRecipient(userRepository.getById(recipientId));
        messageRepository.save(message);
    }
}