package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.comment.dto.CommentDetails;
import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.post.dto.PostRequest;
import io.github.xpakx.micro2.post.dto.PostWithComments;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.post.error.PostTooOldToEditException;
import io.github.xpakx.micro2.tag.Tag;
import io.github.xpakx.micro2.tag.TagService;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;

    @Mock
    private TagService tagService;

    private PostService service;

    private final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    private void injectMocks() {
        service = new PostService(postRepository, commentRepository, userRepository, tagService, null);
    }

    private PostRequest getPostRequestWithContent(String content) {
        PostRequest result = new PostRequest();
        result.setMessage(content);
        return result;
    }

    private UserAccount getUserWithUsername(String username) {
        UserAccount result = new UserAccount();
        result.setUsername(username);
        return result;
    }

    @Test
    void shouldNotAddPostForNonExistentUser() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                UserNotFoundException.class,
                () -> service.addPost(getPostRequestWithContent("Post"), "username")
        );
    }

    @Test
    void shouldAddNewPostWithData() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUserWithUsername("username")));
        given(postRepository.save(ArgumentMatchers.any(Post.class)))
                .willReturn(getEmptyPost());
        injectMocks();

        service.addPost(getPostRequestWithContent("Post"), "username");

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        then(postRepository)
                .should(times(1))
                .save(postCaptor.capture());
        Post result = postCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getUser().getUsername(), is("username"));
        assertThat(result.getContent(), is("Post"));
        assertThat(result.getLikeCount(), is(equalTo(0)));
        assertThat(result.getDislikeCount(), is(equalTo(0)));
        assertNull(result.getId());
        assertNotNull(result.getCreatedAt());
    }

    private Post getEmptyPost() {
        Post result = new Post();
        result.setUser(getUserWithUsername("username"));
        return result;
    }

    @Test
    void shouldAddNewPostWithTags() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUserWithUsername("username")));
        given(postRepository.save(ArgumentMatchers.any(Post.class)))
                .willReturn(getEmptyPost());
        given(tagService.addTags(anyString()))
                .willReturn(Set.of(getTagWithName("tag")));
        injectMocks();

        service.addPost(getPostRequestWithContent("Post #tag"), "username");

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        then(postRepository)
                .should(times(1))
                .save(postCaptor.capture());
        Post result = postCaptor.getValue();

        assertNotNull(result.getTags());
        assertThat(result.getTags(), hasSize(1));
        assertThat(result.getTags(), contains(hasProperty("name", is("tag"))));
    }

    private Tag getTagWithName(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }

    @Test
    void shouldDeletePost() {
        given(postRepository.findByIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getEmptyPost()));
        injectMocks();

        service.deletePost(1L, "username");

        then(postRepository)
                .should(times(1))
                .delete(ArgumentMatchers.any());
    }

    @Test
    void shouldThrowExceptionWhileDeletingNonexistentPost() {
        given(postRepository.findByIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                PostNotFoundException.class,
                () -> service.deletePost(1L, "username")
        );
    }

    @Test
    void shouldThrowExceptionWhileUpdatingNonexistentPost() {
        given(postRepository.findByIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                PostNotFoundException.class,
                () -> service.updatePost(getPostRequestWithContent("edited post"), 1L, "username")
        );
    }

    @Test
    void shouldNotUpdatePostOlderThan24h() {
        given(postRepository.findByIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getOutdatedPost()));
        injectMocks();

        assertThrows(
                PostTooOldToEditException.class,
                () -> service.updatePost(getPostRequestWithContent("edited post"), 1L, "username")
        );
    }

    private Post getOutdatedPost() {
        Post result = new Post();
        result.setUser(getUserWithUsername("username"));
        result.setCreatedAt(LocalDateTime.now().minusDays(2));
        return result;
    }

    private Post getNotOutdatedPost() {
        Post result = new Post();
        result.setUser(getUserWithUsername("username"));
        result.setCreatedAt(LocalDateTime.now());
        return result;
    }

    @Test
    void shouldUpdatePost() {
        given(postRepository.findByIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getNotOutdatedPost()));
        given(postRepository.save(ArgumentMatchers.any(Post.class)))
                .willReturn(getEmptyPost());
        injectMocks();

        service.updatePost(getPostRequestWithContent("update"), 1L,"username");

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        then(postRepository)
                .should(times(1))
                .save(postCaptor.capture());
        Post result = postCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getUser().getUsername(), is("username"));
        assertThat(result.getContent(), is("update"));
        assertThat(result.isEdited(), is(true));
    }

    @Test
    void shouldChangeTagsWhileUpdating() {
        given(postRepository.findByIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getPostWithTag(List.of("tag", "tag2"))));
        given(postRepository.save(ArgumentMatchers.any(Post.class)))
                .willReturn(getEmptyPost());
        given(tagService.addTags(anyString()))
                .willReturn(Set.of(getTagWithName("newTag"), getTagWithName("newTag2")));
        injectMocks();
        injectMocks();

        service.updatePost(getPostRequestWithContent("Post #newTag #newTag2"), 1L, "username");

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        then(postRepository)
                .should(times(1))
                .save(postCaptor.capture());
        Post result = postCaptor.getValue();

        assertNotNull(result.getTags());
        assertThat(result.getTags(), hasSize(2));
        assertThat(result.getTags(), everyItem(either(hasProperty("name", is("newTag")))
                .or(hasProperty("name", is("newTag2")))));
    }

    private Post getPostWithTag(List<String> tag) {
        Post result = new Post();
        result.setUser(getUserWithUsername("username"));
        result.setCreatedAt(LocalDateTime.now());
        result.setTags(tag.stream().map(this::getTagWithName).collect(Collectors.toSet()));
        return result;
    }

    private PostDetails getPostDetails(String content) {
        Post post = new Post();
        post.setContent(content);
        return factory.createProjection(PostDetails.class, post);
    }

    @Test
    void shouldReturnPageWithAllPosts() {
        given(postRepository.findAllBy(ArgumentMatchers.any(PageRequest.class)))
                .willReturn(new PageImpl<>(List.of(getPostDetails("post1"), getPostDetails("post2"))));
        injectMocks();

        Page<PostDetails> result = service.getPosts(0);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertThat(result.getContent(), hasSize(2));
        assertThat(result.getContent(), hasItem(hasProperty("content", is("post1"))));
        assertThat(result.getContent(), hasItem(hasProperty("content", is("post2"))));
    }

    @Test
    void shouldReturnPageWithUserPosts() {
        given(postRepository.getAllByUserUsername(anyString(), ArgumentMatchers.any(PageRequest.class)))
                .willReturn(new PageImpl<>(List.of(getPostDetails("post1"), getPostDetails("post2"))));
        injectMocks();

        Page<PostDetails> result = service.getPostsByUsername(0, "username");

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertThat(result.getContent(), hasSize(2));
        assertThat(result.getContent(), hasItem(hasProperty("content", is("post1"))));
        assertThat(result.getContent(), hasItem(hasProperty("content", is("post2"))));
    }

    @Test
    void shouldReturnPageWithTaggedPosts() {
        given(postRepository.findAllByTagsName(anyString(), ArgumentMatchers.any(PageRequest.class)))
                .willReturn(new PageImpl<>(List.of(getPostDetails("post1"), getPostDetails("post2"))));
        injectMocks();

        Page<PostDetails> result = service.getPostsByTagName(0, "tag");

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertThat(result.getContent(), hasSize(2));
        assertThat(result.getContent(), hasItem(hasProperty("content", is("post1"))));
        assertThat(result.getContent(), hasItem(hasProperty("content", is("post2"))));
    }

    @Test
    void shouldReturnSinglePost() {
        PostDetails post = getPostDetails("post");
        given(postRepository.findProjectedById(anyLong()))
                .willReturn(Optional.of(post));
        injectMocks();

        PostDetails result = service.getSinglePost(1L);

        assertSame(post, result);
    }

    @Test
    void shouldThrowExceptionIfPostDoesNotExist() {
        given(postRepository.findProjectedById(anyLong()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                PostNotFoundException.class,
                () -> service.getSinglePost(1L)
        );
    }

    @Test
    void shouldReturnSinglePostWithComments() {
        PostDetails post = getPostDetails("post");
        Page<CommentDetails> comments = Page.empty();
        given(postRepository.findProjectedById(anyLong()))
                .willReturn(Optional.of(post));
        given(commentRepository.getAllByPostId(anyLong(), ArgumentMatchers.any(Pageable.class)))
                .willReturn(comments);
        injectMocks();

        PostWithComments result = service.getSinglePostWithComments(1L);

        assertSame(post, result.getPost());
        assertSame(comments, result.getComments());
    }

    @Test
    void shouldThrowExceptionIfPostDoesNotExistWhileLoadingPostWithComments() {
        given(postRepository.findProjectedById(anyLong()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                PostNotFoundException.class,
                () -> service.getSinglePostWithComments(1L)
        );
    }
}
