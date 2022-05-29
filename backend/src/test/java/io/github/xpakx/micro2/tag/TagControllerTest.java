package io.github.xpakx.micro2.tag;

import io.github.xpakx.micro2.comment.Comment;
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
import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TagControllerTest {
    @LocalServerPort
    private int port;
    private String baseUrl;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    TagRepository tagRepository;

    Long userId;

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

    private void createTagsWithPosts(Long userId) {
        List<Long> tagIds = new ArrayList<>();
        for(int i = 1; i<=11; i++) {
            tagIds.add(addTag("tag"+i));
        }
        for(Long id : tagIds) {
            addPostsToTag(Math.toIntExact(id), id, userId);
        }
    }

    private void createTagsForAutocompletion() {
        addTag("tag");
        addTag("tag2");
        addTag("label");
    }

    private void addPostsToTag(int n, Long tagId, Long userId) {
        for(int i = 0; i<n; i++) {
            addPost(userId, List.of(tagId));
        }
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Long addPost(Long userId, List<Long> tags) {
        Post post = new Post();
        post.setContent("content");
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(userId));
        post.setCreatedAt(LocalDateTime.now());
        post.setTags(tags.stream().map((i) -> tagRepository.getById(i)).collect(Collectors.toSet()));
        return postRepository.save(post).getId();
    }

    private Long addTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag).getId();
    }
    @Test
    void shouldReturnTop10Tags() {
        createTagsWithPosts(userId);
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/tags/top")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(10))
                .body("$", not(hasItem(hasProperty("name", equalTo("tag11")))));
    }

    @Test
    void shouldSuggestTagCompletions() {
        createTagsForAutocompletion();
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/tags/name?start={name}", "ta")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(2))
                .body("$", not(hasItem(hasProperty("name", equalTo("label")))));
    }
}
