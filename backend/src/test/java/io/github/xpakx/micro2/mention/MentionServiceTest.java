package io.github.xpakx.micro2.mention;

import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MentionServiceTest {
    @Mock
    private MentionRepository mentionRepository;
    @Mock
    private UserRepository userRepository;
    private MentionService service;

    private final ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    private void injectMocks() {
        service = new MentionService(mentionRepository, userRepository);
    }

    private UserAccount getUser(String username) {
        UserAccount author = new UserAccount();
        author.setUsername(username);
        author.setId(1L);
        return author;
    }

    private Post getPost(String content, UserAccount author) {
        Post post = new Post();
        post.setContent(content);
        post.setUser(author);
        post.setId(5L);
        return post;
    }

    @Test
    void shouldNotAddMentionsForPost() {
        String message = "";
        UserAccount author = getUser("user1");
        Post post = getPost(message, author);
        injectMocks();

        List<Mention> result = service.addMentions(message, author, post);

        assertNotNull(result);
        assertThat(result, hasSize(0));
    }

    @Test
    void shouldAddMentionsForPost() {
        String message = "@user2";
        UserAccount author = getUser("user1");
        UserAccount mentioned = getUser("user2");
        Post post = getPost(message, author);
        given(userRepository.findByUsername("user2"))
                .willReturn(Optional.of(mentioned));
        injectMocks();

        List<Mention> result = service.addMentions(message, author, post);

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertThat(result, hasItem(
                hasProperty("mentioned",
                        hasProperty(
                                "username",
                                equalTo("user2")
                        )
                )
        ));
    }

    @Test
    void shouldAddMentionsForPostOnlyForUsersThatExists() {
        String message = "@user2 @user3";
        UserAccount author = getUser("user1");
        UserAccount mentioned = getUser("user2");
        Post post = getPost(message, author);
        given(userRepository.findByUsername("user2"))
                .willReturn(Optional.of(mentioned));
        given(userRepository.findByUsername("user3"))
                .willReturn(Optional.empty());
        injectMocks();

        List<Mention> result = service.addMentions(message, author, post);

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertThat(result, hasItem(
                hasProperty("mentioned",
                        hasProperty(
                                "username",
                                equalTo("user2")
                        )
                )
        ));
    }

    @Test
    void shouldNotAddMentionWithLetterBefore() {
        String message = "@user2 a@user3";
        UserAccount author = getUser("user1");
        UserAccount mentioned = getUser("user2");
        Post post = getPost(message, author);
        given(userRepository.findByUsername("user2"))
                .willReturn(Optional.of(mentioned));
        injectMocks();

        List<Mention> result = service.addMentions(message, author, post);

        assertNotNull(result);
        assertThat(result, hasSize(1));
    }

    @Test
    void shouldNotAddMentionWithoutSpaceBefore() {
        String message = "@user2@user3";
        UserAccount author = getUser("user1");
        UserAccount mentioned = getUser("user2");
        Post post = getPost(message, author);
        given(userRepository.findByUsername("user2"))
                .willReturn(Optional.of(mentioned));
        injectMocks();

        List<Mention> result = service.addMentions(message, author, post);

        assertNotNull(result);
        assertThat(result, hasSize(1));
    }
}