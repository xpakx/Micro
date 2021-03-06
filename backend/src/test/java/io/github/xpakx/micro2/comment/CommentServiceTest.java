package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentDetails;
import io.github.xpakx.micro2.comment.dto.CommentRequest;
import io.github.xpakx.micro2.comment.dto.CommentWithUserData;
import io.github.xpakx.micro2.comment.error.CommentHasRepliesException;
import io.github.xpakx.micro2.comment.error.CommentNotFoundException;
import io.github.xpakx.micro2.comment.error.CommentTooOldToEditException;
import io.github.xpakx.micro2.mention.MentionService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private MentionService mentionService;

    private CommentService service;

    private final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    @BeforeEach
    void setUp() {
    }

    private void injectMocks() {
        service = new CommentService(commentRepository, userRepository, postRepository, mentionService);
    }

    @Test
    void shouldNotAddCommentForNonExistentUser() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                UserNotFoundException.class,
                () -> service.addComment(getCommentRequestWithContent("comment"), "username", 1L)
        );
    }

    private CommentRequest getCommentRequestWithContent(String content) {
        CommentRequest result = new CommentRequest();
        result.setMessage(content);
        return result;
    }

    @Test
    void shouldNotAddCommentToNonExistentPost() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUserWithUsername("username")));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                PostNotFoundException.class,
                () -> service.addComment(getCommentRequestWithContent("comment"), "username", 1L)
        );
    }

    private UserAccount getUserWithUsername(String username) {
        UserAccount result = new UserAccount();
        result.setUsername(username);
        return result;
    }

    @Test
    void shouldAddNewComment() {
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(getUserWithUsername("username")));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(getEmptyPost()));
        given(commentRepository.save(ArgumentMatchers.any(Comment.class)))
                .willReturn(getEmptyComment());
        injectMocks();

        service.addComment(getCommentRequestWithContent("comment"), "username", 1L);

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        then(commentRepository)
                .should(times(1))
                .save(commentCaptor.capture());
        Comment result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getUser().getUsername(), is("username"));
        assertThat(result.getContent(), is("comment"));
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

    private Comment getEmptyComment() {
        Comment result = new Comment();
        result.setUser(getUserWithUsername("username"));
        return result;
    }

    @Test
    void shouldThrowExceptionWhileUpdatingNonexistentComment() {
        given(commentRepository.findByIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                CommentNotFoundException.class,
                () -> service.updateComment(getCommentRequestWithContent("edited comment"), 1L, "username")
        );
    }

    @Test
    void shouldNotUpdateCommentOlderThan24h() {
        given(commentRepository.findByIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getOutdatedComment()));
        injectMocks();

        assertThrows(
                CommentTooOldToEditException.class,
                () -> service.updateComment(getCommentRequestWithContent("edited comment"), 1L, "username")
        );
    }

    private Comment getOutdatedComment() {
        Comment result = new Comment();
        result.setUser(getUserWithUsername("username"));
        result.setCreatedAt(LocalDateTime.now().minusDays(2));
        return result;
    }
    @Test
    void shouldNotUpdateCommentThatHasReplies() {
        given(commentRepository.findByIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getNotOutdatedComment()));
        given(commentRepository.existsByPostIdAndCreatedAtIsGreaterThan(anyLong(), ArgumentMatchers.any(LocalDateTime.class)))
                .willReturn(true);
        injectMocks();

        assertThrows(
                CommentHasRepliesException.class,
                () -> service.updateComment(getCommentRequestWithContent("edited comment"), 1L, "username")
        );
    }

    private Comment getNotOutdatedComment() {
        Comment result = new Comment();
        result.setUser(getUserWithUsername("username"));
        result.setCreatedAt(LocalDateTime.now());
        Post post = new Post();
        post.setId(1L);
        result.setPost(post);
        return result;
    }

    @Test
    void shouldUpdateComment() {
        given(commentRepository.findByIdAndUserUsername(anyLong(), anyString()))
                .willReturn(Optional.of(getNotOutdatedComment()));
        given(commentRepository.existsByPostIdAndCreatedAtIsGreaterThan(anyLong(), ArgumentMatchers.any(LocalDateTime.class)))
                .willReturn(false);
        given(commentRepository.save(ArgumentMatchers.any(Comment.class)))
                .willReturn(getEmptyComment());
        injectMocks();

        service.updateComment(getCommentRequestWithContent("update"), 1L,"username");

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        then(commentRepository)
                .should(times(1))
                .save(commentCaptor.capture());
        Comment result = commentCaptor.getValue();

        assertNotNull(result);
        assertThat(result.getUser().getUsername(), is("username"));
        assertThat(result.getContent(), is("update"));
        assertThat(result.isEdited(), is(true));
    }

    @Test
    void shouldThrowExceptionWhileDeletingNonexistentComment() {
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                CommentNotFoundException.class,
                () -> service.deleteComment(1L, "username")
        );
    }

    @Test
    void shouldDeleteCommentWhenRequestedByCommentAuthor() {
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getCommentByUser("username")));
        injectMocks();

        service.deleteComment(1L, "username");

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        then(commentRepository)
                .should(times(1))
                .save(commentCaptor.capture());
        Comment result = commentCaptor.getValue();

        assertNotNull(result);
        assertTrue(result.isDeletedByUser());
    }

    private Comment getCommentByUser(String username) {
        Comment result = new Comment();
        result.setUser(getUserWithUsername(username));
        result.setId(1L);
        return result;
    }

    @Test
    void shouldDeleteCommentWhenRequestedByPostAuthor() {
        given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(getCommentByUser("user2")));
        given(postRepository.findByCommentsId(anyLong()))
                .willReturn(Optional.of(getPostByUser("user1")));
        injectMocks();

        service.deleteComment(1L, "user1");

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        then(commentRepository)
                .should(times(1))
                .save(commentCaptor.capture());
        Comment result = commentCaptor.getValue();

        assertNotNull(result);
        assertTrue(result.isDeletedByPostAuthor());
    }

    private Post getPostByUser(String username) {
        Post result = new Post();
        result.setUser(getUserWithUsername(username));
        return result;
    }

    @Test
    void shouldReturnSingleComment() {
        CommentDetails comment = getCommentDetails("comment");
        given(commentRepository.findProjectedById(anyLong()))
                .willReturn(Optional.of(comment));
        injectMocks();

        CommentDetails result = service.getSingleComment(1L);

        assertSame(comment, result);
    }

    private CommentDetails getCommentDetails(String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        return factory.createProjection(CommentDetails.class, comment);
    }

    @Test
    void shouldThrowExceptionIfCommentDoesNotExist() {
        given(commentRepository.findProjectedById(anyLong()))
                .willReturn(Optional.empty());
        injectMocks();

        assertThrows(
                CommentNotFoundException.class,
                () -> service.getSingleComment(1L)
        );
    }

    @Test
    void shouldReturnPageWithComments() {
        given(commentRepository.getAllByPostId(anyLong(), ArgumentMatchers.any(PageRequest.class)))
                .willReturn(new PageImpl<>(List.of(getCommentDetails("comment1"), getCommentDetails("comment2"))));
        injectMocks();

        Page<CommentWithUserData> result = service.getCommentsForPost(0, 1L);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertThat(result.getContent(), hasSize(2));
        assertThat(result.getContent(), hasItem(
                    hasProperty("comment",
                            hasProperty("content", is("comment1"))
                    )
                )
        );
        assertThat(result.getContent(), hasItem(
                        hasProperty("comment",
                                hasProperty("content", is("comment2"))
                        )
                )
        );
    }
}