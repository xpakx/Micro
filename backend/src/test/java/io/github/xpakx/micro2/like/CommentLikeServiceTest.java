package io.github.xpakx.micro2.like;

import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.comment.error.CommentNotFoundException;
import io.github.xpakx.micro2.like.dto.LikeDetails;
import io.github.xpakx.micro2.like.dto.LikeRequest;
import io.github.xpakx.micro2.like.error.LikeNotFoundException;
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
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;

    private CommentLikeService service;

    private final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    @BeforeEach
    void setUp() {
    }

    private void injectMocks() {
        service = new CommentLikeService(likeRepository, commentRepository, userRepository);
    }

    @Test
    void shouldNotAddLikeNonExistentComment() {
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                CommentNotFoundException.class,
                () -> service.likeComment(getLikeRequest(true), 1L, "username")
        );
    }

    private LikeRequest getLikeRequest(boolean like) {
        LikeRequest request = new LikeRequest();
        request.setLike(like);
        return request;
    }

    @Test
    void shouldNotLetNonExistentUserLikeComment() {
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getEmptyComment()));
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                UserNotFoundException.class,
                () -> service.likeComment(getLikeRequest(true), 1L, "username")
        );
    }

    private Comment getEmptyComment() {
        Comment result = new Comment();
        result.setLikeCount(0);
        result.setDislikeCount(0);
        result.setId(1L);
        return result;
    }

    @Test
    void shouldCreateNewLike() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUser()));
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getEmptyComment()));
        given(likeRepository.save(ArgumentMatchers.any(Like.class)))
                .willReturn(getEmptyLike());
        injectMocks();

        service.likeComment(getLikeRequest(true), 1L, "username");

        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        then(likeRepository)
                .should(times(1))
                .save(likeCaptor.capture());
        Like result = likeCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getUser().getUsername(), is("username"));
        assertNotNull(result.getComment());
        assertNull(result.getPost());
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
        result.setComment(getEmptyComment());
        result.setUser(getUser());
        return result;
    }

    @Test
    void shouldCreateNewDislike() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUser()));
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getEmptyComment()));
        given(likeRepository.save(ArgumentMatchers.any(Like.class)))
                .willReturn(getEmptyLike());
        injectMocks();

        service.likeComment(getLikeRequest(false), 1L, "username");

        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        then(likeRepository)
                .should(times(1))
                .save(likeCaptor.capture());
        Like result = likeCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getUser().getUsername(), is("username"));
        assertNotNull(result.getComment());
        assertNull(result.getPost());
        assertNull(result.getId());
        assertThat(result.isPositive(), is(false));
    }

    @Test
    void shouldUpdateLikeCount() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUser()));
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getCommentWithLikeCount(5, 6)));
        given(likeRepository.save(ArgumentMatchers.any(Like.class)))
                .willReturn(getEmptyLike());
        injectMocks();

        service.likeComment(getLikeRequest(true), 1L, "username");

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        then(commentRepository)
                .should(times(1))
                .save(commentCaptor.capture());
        Comment result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getLikeCount(), is(equalTo(6)));
        assertThat(result.getDislikeCount(), is(equalTo(6)));
    }

    private Comment getCommentWithLikeCount(int likes, int dislikes) {
        Comment result = new Comment();
        result.setLikeCount(likes);
        result.setDislikeCount(dislikes);
        result.setId(1L);
        return result;
    }

    @Test
    void shouldUpdateDislikeCount() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUser()));
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getCommentWithLikeCount(5, 6)));
        given(likeRepository.save(ArgumentMatchers.any(Like.class)))
                .willReturn(getEmptyLike());
        injectMocks();

        service.likeComment(getLikeRequest(false), 1L, "username");

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        then(commentRepository)
                .should(times(1))
                .save(commentCaptor.capture());
        Comment result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getLikeCount(), is(equalTo(5)));
        assertThat(result.getDislikeCount(), is(equalTo(7)));
    }

    @Test
    void shouldSwitchLike() {
        given(likeRepository.findByCommentIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getLike(true)));
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getCommentWithLikeCount(5, 6)));
        given(likeRepository.save(ArgumentMatchers.any(Like.class)))
                .willReturn(getEmptyLike());
        injectMocks();

        service.likeComment(getLikeRequest(false), 1L, "username");

        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        then(likeRepository)
                .should(times(1))
                .save(likeCaptor.capture());
        Like result = likeCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getUser().getUsername(), is("username"));
        assertNotNull(result.getComment());
        assertNull(result.getPost());
        assertNull(result.getId());
        assertThat(result.isPositive(), is(false));
    }

    private Like getLike(boolean positive) {
        Like result = new Like();
        result.setPositive(positive);
        result.setComment(getEmptyComment());
        result.setUser(getUser());
        return result;
    }

    @Test
    void shouldUpdateLikeCountAfterSwitchingLike() {
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getCommentWithLikeCount(5, 6)));
        given(likeRepository.save(ArgumentMatchers.any(Like.class)))
                .willReturn(getEmptyLike());
        given(likeRepository.findByCommentIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getLike(true)));
        injectMocks();

        service.likeComment(getLikeRequest(false), 1L, "username");

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        then(commentRepository)
                .should(times(1))
                .save(commentCaptor.capture());
        Comment result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getLikeCount(), is(equalTo(4)));
        assertThat(result.getDislikeCount(), is(equalTo(7)));
    }

    @Test
    void shouldThrowExceptionWhileDeletingNonexistentLike() {
        given(likeRepository.findByCommentIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                LikeNotFoundException.class,
                () -> service.unlikeComment(1L, "username")
        );
    }

    @Test
    void shouldUnlikeComment() {
        given(likeRepository.findByCommentIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getLike(true)));
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getCommentWithLikeCount(1, 0)));
        injectMocks();

        service.unlikeComment(1L, "username");

        then(likeRepository)
                .should(times(1))
                .delete(any(Like.class));
    }

    @Test
    void shouldDecreaseLikeCount() {
        given(likeRepository.findByCommentIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getLike(true)));
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getCommentWithLikeCount(1, 0)));
        injectMocks();

        service.unlikeComment(1L, "username");

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        then(commentRepository)
                .should(times(1))
                .save(commentCaptor.capture());
        Comment result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getLikeCount(), is(equalTo(0)));
        assertThat(result.getDislikeCount(), is(equalTo(0)));
    }

    @Test
    void shouldThrowExceptionIfLikeDoesNotExist() {
        given(likeRepository.findProjectedByCommentIdAndUserUsername(anyLong(), anyString(), any(Class.class)))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                LikeNotFoundException.class,
                () -> service.getLike(1L, "username")
        );
    }

    @Test
    void shouldReturnLike() {
        LikeDetails like = getLikeDetails(true);
        given(likeRepository.findProjectedByCommentIdAndUserUsername(anyLong(), anyString(), any(Class.class)))
                .willReturn(Optional.of(like));
        injectMocks();

        LikeDetails result = service.getLike(1L, "username");

        assertSame(like, result);
    }

    private LikeDetails getLikeDetails(boolean positive) {
        Like like = new Like();
        like.setPositive(positive);
        like.setId(1L);
        return factory.createProjection(LikeDetails.class, like);
    }
}
