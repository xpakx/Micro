package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentDto;
import io.github.xpakx.micro2.comment.dto.CommentRequest;
import io.github.xpakx.micro2.comment.error.CommentNotFoundException;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentDto addComment(CommentRequest request, String username, Long postId) {
        Comment newComment = new Comment();
        newComment.setContent(request.getMessage());
        newComment.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Not such user!"))
        );
        newComment.setPost(postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Not such post!"))
        );
        newComment.setEdited(false);
        newComment.setCreatedAt(LocalDateTime.now());
        return CommentDto.fromComment(commentRepository.save(newComment));
    }

    public CommentDto updateComment(CommentRequest request, Long commentId, String username) {
        Comment toUpdate = commentRepository.findByIdAndUserUsername(commentId, username)
                .orElseThrow(CommentNotFoundException::new);
        toUpdate.setContent(request.getMessage());
        toUpdate.setEdited(true);
        return CommentDto.fromComment(commentRepository.save(toUpdate));
    }
}
