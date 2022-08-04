package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.ModerationRequest;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ModerationService {
    private final ModerationRepository moderationRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public Moderation moderatePost(ModerationRequest request, Long postId, String username) {
        UserAccount moderator = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        post.setDeleted(true);
        post = postRepository.save(post);
        LocalDateTime now = LocalDateTime.now();
        Moderation moderation = new Moderation();
        moderation.setModerated(true);
        moderation.setModeratedAt(now);
        moderation.setCreatedAt(now);
        moderation.setReason(request.getReason());
        moderation.setAuthor(moderator);
        moderation.setModerator(moderator);
        moderation.setPost(post);
        return moderationRepository.save(moderation);
    }
}
