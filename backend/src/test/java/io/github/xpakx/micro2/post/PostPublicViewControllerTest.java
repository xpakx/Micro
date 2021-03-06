package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.fav.FavPost;
import io.github.xpakx.micro2.fav.FavPostRepository;
import io.github.xpakx.micro2.like.Like;
import io.github.xpakx.micro2.like.LikeRepository;
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
    private long activeUserId;

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
    LikeRepository likeRepository;
    @Autowired
    FavPostRepository favPostRepository;
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
        Long userId = user.getId();
        activeUserId = userId;
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
        likeRepository.deleteAll();
        favPostRepository.deleteAll();
        commentRepository.deleteAll();
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
                .get(baseUrl + "/users/{username}/posts", "user1")
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(3))
                .body("content.post.content", not(hasItem(equalTo("post1"))))
                .body("content.post.content", not(hasItem(equalTo("post2"))))
                .body("content.post.content", hasItem(equalTo("post3")))
                .body("content.post.content", hasItem(equalTo("post4")))
                .body("content.post.content", hasItem(equalTo("post5")));
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
                .body("content.post.content", not(hasItem(equalTo("post1"))))
                .body("content.post.content", hasItem(equalTo("post2")))
                .body("content.post.content", hasItem(equalTo("post3")))
                .body("content.post.content", not(hasItem(equalTo("post4"))))
                .body("content.post.content", hasItem(equalTo("post5")));
    }

    @Test
    void shouldReturnPost() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/posts/{postId}/min", postId)
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
                .get(baseUrl + "/posts/{postId}/min", maxPostId+1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldReturnPostWithComments() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/posts/{postId}", postId)
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
                .get(baseUrl + "/posts/{postId}", maxPostId+1)
        .then()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void shouldReturnAllPostsWithUserData() {
        likePost("user1", postId);
        favPost("user1", maxPostId);
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/posts")
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(5))
                .body("content.findAll { it.post.id=="+postId+" }.liked", hasItem(true))
                .body("content.findAll { it.post.id!="+postId+" }.liked", not(hasItem(true)))
                .body("content.findAll { it.post.id=="+maxPostId+" }.fav", hasItem(true))
                .body("content.findAll { it.post.id!="+maxPostId+" }.fav", not(hasItem(true)));
    }

    @Test
    void shouldReturnAllPostsByUserWithUserData() {
        likePost("user1", maxPostId);
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/users/{username}/posts", "user1")
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(3))
                .body("content.findAll { it.post.id=="+maxPostId+" }.liked", hasItem(true))
                .body("content.findAll { it.post.id!="+maxPostId+" }.liked", not(hasItem(true)));
    }

    private void likePost(String username, Long postId) {
        Like like = new Like();
        like.setPost(postRepository.findById(postId).get());
        like.setUser(userRepository.findByUsername(username).get());
        like.setPositive(true);
        likeRepository.save(like);
    }

    private void favPost(String username, Long postId) {
        FavPost favPost = new FavPost();
        favPost.setPost(postRepository.findById(postId).get());
        favPost.setUser(userRepository.findByUsername(username).get());
        favPostRepository.save(favPost);
    }

    @Test
    void shouldReturnAllPostsByTagWithUserData() {
        likePost("user1", maxPostId);
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/tags/{tag}/posts", "tag1")
        .then()
                .statusCode(OK.value())
                .body("content", hasSize(3))
                .body("content.findAll { it.post.id=="+maxPostId+" }.liked", hasItem(true))
                .body("content.findAll { it.post.id!="+maxPostId+" }.liked", not(hasItem(true)));
    }

    @Test
    void shouldReturnAllHotPosts() {
        setLikes(maxPostId, 10);
        setLikes(postId, 9);
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/posts/hot")
        .then()
                .statusCode(OK.value())
                .body("content[0].post.content", equalTo("post5"))
                .body("content[1].post.content", equalTo("post1"));
    }

    private void setLikes(Long postId, int i) {
        Post post = postRepository.findById(postId).get();
        post.setLikeCount(i);
        postRepository.save(post);
    }

    @Test
    void shouldReturnHotPostsWithUserData() {
        setLikes(maxPostId, 10);
        likePost("user1", maxPostId);
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/posts/hot")
        .then()
                .statusCode(OK.value())
                .body("content[0].post.content", equalTo("post5"))
                .body("content[0].liked", is(true))
                .body("content.findAll { it.post.id!="+maxPostId+" }.liked", not(hasItem(true)));
    }

    @Test
    void shouldReturnAllActivePosts() {
        addComment(maxPostId, "comment1");
        addComment(maxPostId, "comment2");
        addComment(postId, "comment3");
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/posts/active")
        .then()
                .statusCode(OK.value())
                .body("content[0].post.content", equalTo("post5"))
                .body("content[1].post.content", equalTo("post1"));
    }

    private void addComment(Long postId, String content) {
        Comment comment = new Comment();
        comment.setUser(userRepository.getById(activeUserId));
        comment.setPost(postRepository.findById(postId).get());
        comment.setContent(content);
        comment.setLikeCount(0);
        comment.setDislikeCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    private void addComment(Long postId, String content, LocalDateTime dateTime) {
        Comment comment = new Comment();
        comment.setUser(userRepository.getById(activeUserId));
        comment.setPost(postRepository.findById(postId).get());
        comment.setContent(content);
        comment.setLikeCount(0);
        comment.setDislikeCount(0);
        comment.setCreatedAt(dateTime);
        commentRepository.save(comment);
    }

    @Test
    void shouldReturnActivePostsWithUserData() {
        addComment(maxPostId, "comment1");
        addComment(maxPostId, "comment2");
        likePost("user1", maxPostId);
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/posts/active")
        .then()
                .statusCode(OK.value())
                .body("content[0].post.content", equalTo("post5"))
                .body("content[0].liked", is(true))
                .body("content.findAll { it.post.id!="+maxPostId+" }.liked", not(hasItem(true)));
    }

    @Test
    void shouldReturn2RandomHotPosts() {
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/posts/hot/random")
        .then()
                .statusCode(OK.value())
                .body("$", hasSize(2));
    }

    @Test
    void shouldReturn2CommentsWithActivePosts() {
        addComment(maxPostId, "comment1", LocalDateTime.now().minusMinutes(3));
        addComment(maxPostId, "comment2", LocalDateTime.now().minusMinutes(2));
        addComment(maxPostId, "comment3", LocalDateTime.now());
        likePost("user1", maxPostId);
        given()
                .log()
                .uri()
                .auth()
                .oauth2(tokenFor("user1"))
        .when()
                .get(baseUrl + "/posts/active")
        .then()
                .statusCode(OK.value())
                .body("content.find { it.post.id=="+maxPostId+" }.comments.content", hasSize(2))
                .body("content.find { it.post.id=="+maxPostId+" }.comments.content.comment.content", hasItem(equalTo("comment2")))
                .body("content.find { it.post.id=="+maxPostId+" }.comments.content.comment.content", hasItem(equalTo("comment3")));
    }

    @Test
    void shouldReturn2CommentsWithHotPosts() {
        addComment(maxPostId, "comment1", LocalDateTime.now().minusMinutes(3));
        addComment(maxPostId, "comment2", LocalDateTime.now().minusMinutes(2));
        addComment(maxPostId, "comment3", LocalDateTime.now());
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/posts/hot")
        .then()
                .statusCode(OK.value())
                .body("content.find { it.post.id=="+maxPostId+" }.comments.content", hasSize(2))
                .body("content.find { it.post.id=="+maxPostId+" }.comments.content.comment.content", hasItem(equalTo("comment2")))
                .body("content.find { it.post.id=="+maxPostId+" }.comments.content.comment.content", hasItem(equalTo("comment3")));
    }

    @Test
    void shouldReturn2CommentsWithAllPosts() {
        addComment(maxPostId, "comment1", LocalDateTime.now().minusMinutes(3));
        addComment(maxPostId, "comment2", LocalDateTime.now().minusMinutes(2));
        addComment(maxPostId, "comment3", LocalDateTime.now());
        given()
                .log()
                .uri()
        .when()
                .get(baseUrl + "/posts")
        .then()
                .statusCode(OK.value())
                .body("content.find { it.post.id=="+maxPostId+" }.comments.content", hasSize(2))
                .body("content.find { it.post.id=="+maxPostId+" }.comments.content.comment.content", hasItem(equalTo("comment2")))
                .body("content.find { it.post.id=="+maxPostId+" }.comments.content.comment.content", hasItem(equalTo("comment3")));
    }
}
