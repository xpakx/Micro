package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.fav.FavPost;
import io.github.xpakx.micro2.fav.FavPostRepository;
import io.github.xpakx.micro2.follows.FollowsRepository;
import io.github.xpakx.micro2.follows.UserFollows;
import io.github.xpakx.micro2.mention.Mention;
import io.github.xpakx.micro2.mention.MentionRepository;
import io.github.xpakx.micro2.post.dto.PostRequest;
import io.github.xpakx.micro2.security.JwtTokenUtils;
import io.github.xpakx.micro2.tag.Tag;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerTest {
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
    TagRepository tagRepository;
    @Autowired
    FavPostRepository favRepository;
    @Autowired
    FollowsRepository followsRepository;
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
        favRepository.deleteAll();
        followsRepository.deleteAll();
        postRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String tokenFor(String username) {
        return jwtTokenUtil.generateToken(userService.loadUserByUsername(username));
    }

    @Test
    void shouldRespondWith401ToAddPostIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .post(baseUrl + "/posts")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldAddNewPost() {
        PostRequest request = getValidPostRequest("content");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/posts")
        .then()
                .statusCode(CREATED.value())
                .body("message", equalTo(request.getMessage()))
                .body("username", equalTo("user1"))
                .body("likeCount", equalTo(0))
                .body("dislikeCount", equalTo(0));
    }

    private PostRequest getValidPostRequest(String content) {
        PostRequest request = new PostRequest();
        request.setMessage(content);
        return request;
    }

    @Test
    void shouldAddNewPostWithTag() {
        PostRequest request = getValidPostRequest("content #tag");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/posts")
        .then()
                .statusCode(CREATED.value())
                .body("message", equalTo(request.getMessage()));

        Optional<Tag> tagInDb = tagRepository.findByName("tag");
        assertTrue(tagInDb.isPresent());
    }

    @Test
    void shouldRespondWith401ToUpdatePostIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .put(baseUrl + "/posts/{postId}", 1)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotUpdatePostByOtherUser() {
        Long id = addPostAndReturnId();
        createUser("user2");
        PostRequest request = getValidPostRequest("post");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user2"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/posts/{postId}", id)
        .then()
                .statusCode(NOT_FOUND.value());
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

    private void createUser(String username) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        userRepository.save(user);
    }

    @Test
    void shouldRespondWith404WhileUpdatingNonExistentPost() {
        PostRequest request = getValidPostRequest("post");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/posts/{postId}", 1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldNotUpdatePostOlderThan24Hours() {
        Long id = addOutdatedPostAndReturnId();
        PostRequest request = getValidPostRequest("post");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/posts/{postId}", id)
        .then()
                .statusCode(BAD_REQUEST.value());
    }

    private Long addOutdatedPostAndReturnId() {
        Post post = new Post();
        post.setContent("content");
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(userId));
        post.setCreatedAt(LocalDateTime.now().minusDays(2));
        return postRepository.save(post).getId();
    }
    @Test
    void shouldUpdatePost() {
        Long id = addPostAndReturnId();
        PostRequest request = getValidPostRequest("updated");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/posts/{postId}", id)
        .then()
                .statusCode(OK.value())
                .body("message", is("updated"));
    }

    @Test
    void shouldAddTagWhileUpdatingPost() {
        Long id = addPostAndReturnId();
        PostRequest request = getValidPostRequest("updated #tag");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .put(baseUrl + "/posts/{postId}", id)
        .then()
                .statusCode(OK.value());

        Optional<Tag> tagInDb = tagRepository.findByName("tag");
        assertTrue(tagInDb.isPresent());
    }

    @Test
    void shouldRespondWith401ToDeletePostIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .delete(baseUrl + "/posts/{postId}", 1)
        .then()
                .statusCode(UNAUTHORIZED.value());
    }

    @Test
    void shouldNotDeletePostByOtherUser() {
        Long id = addPostAndReturnId();
        createUser("user2");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user2"))
        .when()
                .delete(baseUrl + "/posts/{postId}", id)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldRespondWith404WhileDeletingNonExistentPost() {
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
        .when()
                .delete(baseUrl + "/posts/{postId}", 1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldDeletePost() {
        Long id = addPostAndReturnId();
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
        .when()
                .delete(baseUrl + "/posts/{postId}", id)
        .then()
                .statusCode(OK.value());
    }

    @Test
    void shouldRespondWith401ToGetFavIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .delete(baseUrl + "/posts/fav")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }


    @Test
    void shouldReturnAllFavPosts() {
        addPostFavByUser("fav-post1", userId);
        addPostFavByUser("fav-post2", userId);
        addPostFavByUser("fav-post3", addUserAndReturnId("user2"));
        addPostAndReturnId();
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/posts/fav")
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(2))
                .body("content.post.content", hasItem(equalTo("fav-post1")))
                .body("content.post.content", hasItem(equalTo("fav-post2")))
                .body("content.post.content", not(hasItem(equalTo("fav-post3"))))
                .body("content.post.content", not(hasItem(equalTo("content"))));
    }

    private Long addUserAndReturnId(String username) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword("password");
        user.setRoles(new HashSet<>());
        return userRepository.save(user).getId();
    }


    private void addPostFavByUser(String content, Long userId) {
        Post post = new Post();
        post.setContent(content);
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(this.userId));
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        FavPost fav = new FavPost();
        fav.setUser(userRepository.getById(userId));
        fav.setPost(savedPost);
        favRepository.save(fav);
    }

    @Test
    void shouldRespondWith401ToGetPostsForFollowedTagsIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .delete(baseUrl + "/posts/follows/tags")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }


    @Test
    void shouldReturnAllPostsForFollowedTags() {
        Tag tag1 = addTag("tag1");
        Tag tag2 = addTag("tag2");
        followTagByUser(tag1, userId);
        addPostWithTag("#tag1 post1", Set.of(tag1));
        addPostWithTag("#tag1 #tag2 fav-post2", Set.of(tag1, tag2));
        addPostWithTag("#tag2 fav-post2", Set.of(tag2));
        addPostWithTag("fav-post2", Set.of());

        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
         .when()
                .get(baseUrl + "/posts/follows/tags")
         .then()
                .statusCode(OK.value())
                .body("content", hasSize(2))
                .body("content.post.content", everyItem(containsString("#tag1")));
    }

    private Tag addTag(String tagName) {
        Tag tag = new Tag();
        tag.setName(tagName);
        return tagRepository.save(tag);
    }

    private void addPostWithTag(String content, Set<Tag> tags) {
        Post post = new Post();
        post.setContent(content);
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(userId));
        post.setCreatedAt(LocalDateTime.now());
        post.setTags(tags);
        postRepository.save(post);
    }

    private void followTagByUser(Tag tag, Long userId) {
        UserFollows follows = new UserFollows();
        follows.setUser(userRepository.getById(userId));
        follows.setTags(Set.of(tag));
        followsRepository.save(follows);
    }

    @Test
    void shouldRespondWith401ToGetPostsByFollowedUsersIfUserUnauthorized() {
        given()
                .log()
                .uri()
        .when()
                .delete(baseUrl + "/posts/follows/users")
        .then()
                .statusCode(UNAUTHORIZED.value());
    }


    @Test
    void shouldReturnAllPostsByFollowedUsers() {
        Long user2Id = addUserAndReturnId("user2");
        Long user3Id = addUserAndReturnId("user3");
        followUserByUser(user2Id, userId);
        addPostByUserReturnId("post1", user2Id);
        addPostByUserReturnId("post2", user2Id);
        addPostByUserReturnId("post3", user3Id);

        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/posts/follows/users")
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(2))
                .body("content.post.content", hasItem(equalTo("post1")))
                .body("content.post.content", hasItem(equalTo("post2")))
                .body("content.post.content", not(hasItem(equalTo("post3"))));
    }

    private void followUserByUser(Long followedId, Long followerId) {
        UserFollows follows = new UserFollows();
        follows.setUser(userRepository.getById(followerId));
        follows.setUsers(Set.of(userRepository.getById(followedId)));
        followsRepository.save(follows);
    }

    private Long addPostByUserReturnId(String content, Long authorId) {
        Post post = new Post();
        post.setContent(content);
        post.setLikeCount(0);
        post.setDislikeCount(0);
        post.setUser(userRepository.getById(authorId));
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post).getId();
    }

    @Test
    void shouldAddNewPostWithMention() {
        addUserAndReturnId("user2");
        PostRequest request = getValidPostRequest("content @user2");
        given()
                .log()
                .uri().auth()
                .oauth2(tokenFor("user1"))
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post(baseUrl + "/posts")
        .then()
                .statusCode(CREATED.value())
                .body("message", equalTo(request.getMessage()));

        List<Mention> mentions = mentionRepository.findAllByMentionedUsernameAndReadIsFalse("user2");
        assertThat(mentions, hasSize(1));
        assertThat(mentions.get(0), hasProperty("read", equalTo(false)));
    }
}
