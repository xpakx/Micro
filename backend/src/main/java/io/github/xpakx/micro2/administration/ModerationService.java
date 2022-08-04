package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.ModerationRequest;
import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.comment.error.CommentNotFoundException;
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
    private final CommentRepository commentRepository;

    @Transactional
    public Moderation moderatePost(ModerationRequest request, Long postId, String username) {
        UserAccount moderator = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        post.setDeleted(true);
        post = postRepository.save(post);
        Moderation moderation = getModerationObject(request, moderator);
        moderation.setPost(post);
        return moderationRepository.save(moderation);
    }

    private Moderation getModerationObject(ModerationRequest request, UserAccount moderator) {
        LocalDateTime now = LocalDateTime.now();
        Moderation moderation = new Moderation();
        moderation.setModerated(true);
        moderation.setModeratedAt(now);
        moderation.setCreatedAt(now);
        moderation.setReason(request.getReason());
        moderation.setAuthor(moderator);
        moderation.setModerator(moderator);
        return moderation;
    }

    @Transactional
    public Moderation moderateComment(ModerationRequest request, Long commentId, String username) {
        UserAccount moderator = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        comment.setDeletedByModerator(true);
        comment = commentRepository.save(comment);
        Moderation moderation = getModerationObject(request, moderator);
        moderation.setComment(comment);
        return moderationRepository.save(moderation);
    }

    @Transactional
    public Moderation moderate(ModerationRequest request, Long modId, String username) {
        UserAccount moderator = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Moderation moderation = moderationRepository.findById(modId)
                .orElseThrow();
        if(moderation.getComment() != null) {
            Comment comment = moderation.getComment();
            comment.setDeletedByModerator(true);
            commentRepository.save(comment);
        } else if(moderation.getPost() != null) {
            Post post = moderation.getPost();
            post.setDeleted(true);
            postRepository.save(post);
        }
        moderation.setModerated(true);
        moderation.setModeratedAt(LocalDateTime.now());
        moderation.setReason(request.getReason());
        moderation.setModerator(moderator);
        return moderationRepository.save(moderation);
    }
}
