package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.post.dto.PostDto;
import io.github.xpakx.micro2.post.dto.PostRequest;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    private void injectMocks() {
        service = new PostService(postRepository, commentRepository, userRepository, tagService);
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
}