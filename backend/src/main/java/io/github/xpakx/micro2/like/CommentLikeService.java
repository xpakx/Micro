package io.github.xpakx.micro2.like;

import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.comment.error.CommentNotFoundException;
import io.github.xpakx.micro2.like.dto.CommentLikeDto;
import io.github.xpakx.micro2.like.dto.LikeDetails;
import io.github.xpakx.micro2.like.dto.LikeRequest;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentLikeService {
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentLikeDto likeComment(LikeRequest request, Long commentId, String username) {
        Optional<Like> like = likeRepository.findByCommentIdAndUserUsername(commentId, username);
        if(like.isEmpty()) {
            return CommentLikeDto.from(createNewLike(request, commentId, username));
        } else if(request.isLike() != like.get().isPositive()) {
            return CommentLikeDto.from(switchLike(request, commentId, like.get()));
        } else {
            return CommentLikeDto.from(like.get());
        }
    }

    private Like switchLike(LikeRequest request, Long commentId, Like toUpdate) {
        toUpdate.setPositive(request.isLike());
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        comment.setLikeCount(request.isLike() ? comment.getLikeCount()+1 : comment.getLikeCount()-1);
        comment.setDislikeCount(request.isLike() ? comment.getDislikeCount()-1 : comment.getDislikeCount()+1);
        commentRepository.save(comment);
        return likeRepository.save(toUpdate);
    }

    private Like createNewLike(LikeRequest request, Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        comment.setLikeCount(request.isLike() ? comment.getLikeCount()+1 : comment.getLikeCount());
        comment.setDislikeCount(request.isLike() ? comment.getDislikeCount() : comment.getDislikeCount()+1);
        Like newLike = new Like();
        newLike.setComment(comment);
        newLike.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Not such user!"))
        );
        newLike.setPositive(request.isLike());
        commentRepository.save(comment);
        return likeRepository.save(newLike);
    }

    @Transactional
    public void unlikeComment(Long commentId, String username) {
        Like like = likeRepository.findByCommentIdAndUserUsername(commentId, username)
                .orElseThrow();
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        comment.setLikeCount(like.isPositive() ? comment.getLikeCount()-1 : comment.getLikeCount());
        comment.setDislikeCount(like.isPositive() ? comment.getDislikeCount() : comment.getDislikeCount()-1);
        commentRepository.save(comment);
        likeRepository.delete(like);
    }

    public LikeDetails getLike(Long commentId, String username) {
        return likeRepository.findProjectedByCommentIdAndUserUsername(commentId, username, LikeDetails.class)
                .orElseThrow();
    }
}
