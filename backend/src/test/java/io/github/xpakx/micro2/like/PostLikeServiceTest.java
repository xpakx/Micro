package io.github.xpakx.micro2.like;

import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.like.dto.LikeRequest;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

    private  PostLikeService service;

    @BeforeEach
    void setUp() {
    }

    private void injectMocks() {
        service = new PostLikeService(likeRepository, postRepository, userRepository);
    }

    @Test
    void shouldNotAddLikeNonExistentPost() {
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                PostNotFoundException.class,
                () -> service.likePost(getLikeRequest(true), 1L, "username")
        );
    }

    private LikeRequest getLikeRequest(boolean like) {
        LikeRequest request = new LikeRequest();
        request.setLike(like);
        return request;
    }

    @Test
    void shouldNotLetNonExistentUserLikePost() {
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(getEmptyPost()));
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                UserNotFoundException.class,
                () -> service.likePost(getLikeRequest(true), 1L, "username")
        );
    }

    private Post getEmptyPost() {
        Post result = new Post();
        result.setLikeCount(0);
        result.setDislikeCount(0);
        result.setId(1L);
        return result;
    }

    @Test
    void shouldCreateNewLike() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUser()));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(getEmptyPost()));
        given(likeRepository.save(ArgumentMatchers.any(Like.class)))
                .willReturn(getEmptyLike());
        injectMocks();

        service.likePost(getLikeRequest(true), 1L, "username");

        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        then(likeRepository)
                .should(times(1))
                .save(likeCaptor.capture());
        Like result = likeCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getUser().getUsername(), is("username"));
        assertNull(result.getComment());
        assertNotNull(result.getPost());
        assertNull(result.getId());
        assertThat(result.isPositive(), is(true));
    }

    private UserAccount getUser() {
        UserAccount result = new UserAccount();
        result.setUsername("username");
        return result;
    }

    private Like getEmptyLike() {
        Like result = new Like();
        result.setPositive(true);
        result.setPost(getEmptyPost());
        result.setUser(getUser());
        return result;
    }

    @Test
    void shouldCreateNewDislike() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUser()));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(getEmptyPost()));
        given(likeRepository.save(ArgumentMatchers.any(Like.class)))
                .willReturn(getEmptyLike());
        injectMocks();

        service.likePost(getLikeRequest(false), 1L, "username");

        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        then(likeRepository)
                .should(times(1))
                .save(likeCaptor.capture());
        Like result = likeCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getUser().getUsername(), is("username"));
        assertNull(result.getComment());
        assertNotNull(result.getPost());
        assertNull(result.getId());
        assertThat(result.isPositive(), is(false));
    }

    @Test
    void shouldUpdateLikeCount() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUser()));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(getPostWithLikeCount(5, 6)));
        given(likeRepository.save(ArgumentMatchers.any(Like.class)))
                .willReturn(getEmptyLike());
        injectMocks();

        service.likePost(getLikeRequest(true), 1L, "username");

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        then(postRepository)
                .should(times(1))
                .save(postCaptor.capture());
        Post result = postCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getLikeCount(), is(equalTo(6)));
        assertThat(result.getDislikeCount(), is(equalTo(6)));
    }

    private Post getPostWithLikeCount(int likes, int dislikes) {
        Post result = new Post();
        result.setLikeCount(likes);
        result.setDislikeCount(dislikes);
        result.setId(1L);
        return result;
    }

    @Test
    void shouldUpdateDislikeCount() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUser()));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(getPostWithLikeCount(5, 6)));
        given(likeRepository.save(ArgumentMatchers.any(Like.class)))
                .willReturn(getEmptyLike());
        injectMocks();

        service.likePost(getLikeRequest(false), 1L, "username");

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        then(postRepository)
                .should(times(1))
                .save(postCaptor.capture());
        Post result = postCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getLikeCount(), is(equalTo(5)));
        assertThat(result.getDislikeCount(), is(equalTo(7)));
    }
}