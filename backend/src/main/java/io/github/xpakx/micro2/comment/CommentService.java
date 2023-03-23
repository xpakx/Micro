package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.*;
import io.github.xpakx.micro2.comment.error.CannotDeleteCommentException;
import io.github.xpakx.micro2.comment.error.CommentHasRepliesException;
import io.github.xpakx.micro2.comment.error.CommentNotFoundException;
import io.github.xpakx.micro2.comment.error.CommentTooOldToEditException;
import io.github.xpakx.micro2.mention.MentionService;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.FileSaveException;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MentionService mentionService;
    private final Path root = Paths.get("attachments");

    public CommentDto addComment(CommentRequest request, String username, Long postId) {
        Comment newComment = new Comment();
        newComment.setContent(request.getMessage());
        UserAccount user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Not such user!"));
        newComment.setUser(user);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Not such post!"));
        newComment.setPost(post);
        newComment.setEdited(false);
        newComment.setCreatedAt(LocalDateTime.now());
        newComment.setLikeCount(0);
        newComment.setDislikeCount(0);
        newComment.setDeletedByUser(false);
        newComment.setDeletedByPostAuthor(false);
        newComment.setMentions(mentionService.addMentions(request.getMessage(), user, post, newComment));
        if(request.getEncodedAttachment() != null && request.getEncodedAttachment().length() > 0) {
            byte[] imageByte = Base64.decodeBase64(
                    request.getEncodedAttachment().contains(",") ? request.getEncodedAttachment().split(",")[1] : request.getEncodedAttachment()
            );
            try {
                String filename = username + "_" + LocalDateTime.now();
                Files.deleteIfExists(this.root.resolve(filename));
                ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByte);
                Files.copy(imageStream, this.root.resolve(filename));
                imageStream.close();
                newComment.setAttachmentUrl(filename);
            } catch (IOException ex) {
                throw new FileSaveException();
            }
        }
        return CommentDto.fromComment(commentRepository.save(newComment));
    }

    public CommentDto updateComment(CommentRequest request, Long commentId, String username) {
        Comment toUpdate = commentRepository.findByIdAndUserUsername(commentId, username)
                .orElseThrow(CommentNotFoundException::new);
        if(toUpdate.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
            throw new CommentTooOldToEditException();
        }
        if(commentRepository.existsByPostIdAndCreatedAtIsGreaterThan(toUpdate.getPost().getId(), toUpdate.getCreatedAt())) {
            throw new CommentHasRepliesException();
        }
        toUpdate.setContent(request.getMessage());
        toUpdate.setEdited(true);
        return CommentDto.fromComment(commentRepository.save(toUpdate));
    }

    @Transactional
    public void deleteComment(Long id, String username) {
        Comment toDelete = commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
        if(toDelete.getUser().getUsername().equals(username)) {
            toDelete.setDeletedByUser(true);
        } else {
            Post post = postRepository.findByCommentsId(toDelete.getId())
                    .orElseThrow(PostNotFoundException::new);
            if(!post.getUser().getUsername().equals(username)) {
                throw new CannotDeleteCommentException();
            }
            toDelete.setDeletedByPostAuthor(true);
        }
        commentRepository.save(toDelete);
    }

    public Page<CommentWithUserData> getCommentsForPost(Integer page, Long postId) {
        Page<CommentDetails> comments = commentRepository.getAllByPostId(
                postId,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
        List<CommentWithUserData> result = comments.stream()
                .map(
                        (c) -> CommentWithUserData.of(c, new CommentUserInfo())
                ).collect(Collectors.toList());
        return new PageImpl<CommentWithUserData>(result, comments.getPageable(), comments.getTotalElements());
    }

    public Page<CommentWithUserData> getCommentsForPostAuth(Integer page, Long postId, String username) {
        Page<CommentDetails> comments = commentRepository.getAllByPostId(
                postId,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
        List<Long> commentIds = comments.getContent().stream()
                .map(CommentDetails::getId)
                .collect(Collectors.toList());
        Map<Long, CommentUserInfo> userInfoMap = commentRepository.getUserInfoMapForCommentIds(commentIds, username);
        List<CommentWithUserData> result = comments.stream()
                .map(
                        (c) -> CommentWithUserData.of(c, userInfoMap.get(c.getId()))
                ).collect(Collectors.toList());
        return new PageImpl<CommentWithUserData>(result, comments.getPageable(), comments.getTotalElements());
    }

    public CommentDetails getSingleComment(Long commentId) {
        return commentRepository.findProjectedById(commentId)
                .orElseThrow(CommentNotFoundException::new);
    }

    public Page<CommentSearchDetails> searchComments(String search, Integer page) {
        return commentRepository.findByContentIsContainingIgnoreCaseAndDeletedByUserIsFalseAndDeletedByPostAuthorIsFalseAndDeletedByModeratorIsFalse(search, PageRequest.of(page, 20));
    }
}
