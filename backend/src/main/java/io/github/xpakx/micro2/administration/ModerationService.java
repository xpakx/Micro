package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.ModerationDetails;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        moderation.setAuthor(post.getUser());
        return moderationRepository.save(moderation);
    }

    private Moderation getModerationObject(ModerationRequest request, UserAccount moderator) {
        LocalDateTime now = LocalDateTime.now();
        Moderation moderation = new Moderation();
        moderation.setModerated(true);
        moderation.setDeleted(true);
        moderation.setModeratedAt(now);
        moderation.setCreatedAt(now);
        moderation.setReason(request.getReason());
        moderation.setModerator(moderator);
        moderation.setReportedBy(moderator);
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
        moderation.setAuthor(comment.getUser());
        return moderationRepository.save(moderation);
    }

    @Transactional
    public Moderation moderate(ModerationRequest request, Long modId, String username) {
        UserAccount moderator = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        Moderation moderation = moderationRepository.findById(modId)
                .orElseThrow();
        moderation.setDeleted(true);
        if(request.isDelete()) {
            if (moderation.getComment() != null) {
                Comment comment = moderation.getComment();
                comment.setDeletedByModerator(true);
                commentRepository.save(comment);
            } else if (moderation.getPost() != null) {
                Post post = moderation.getPost();
                post.setDeleted(true);
                postRepository.save(post);
            }
            moderation.setDeleted(false);
        }
        moderation.setModerated(true);
        moderation.setModeratedAt(LocalDateTime.now());
        moderation.setReason(request.getReason());
        moderation.setModerator(moderator);
        return moderationRepository.save(moderation);
    }

    public Page<ModerationDetails> getUnmoderated(Integer page) {
        return moderationRepository.findAllByModeratedIsFalse(
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
    }

    public Page<ModerationDetails> getAll(Integer page) {
        return moderationRepository.findAllBy(
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
    }

    public Page<ModerationDetails> getForUser(Integer page, String username) {
        return moderationRepository.findByModeratedTrueAndDeletedTrueAndAuthorUsername(
                username,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
    }

    public Page<ModerationDetails> getUserReports(Integer page, String username) {
        return moderationRepository.findByReportedByUsername(
                username,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
    }
}
