package io.github.xpakx.micro2.like;

import io.github.xpakx.micro2.comment.CommentRepository;
import io.github.xpakx.micro2.like.dto.LikeRequest;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Like likePost(LikeRequest request, Long postId, String username) {
        Optional<Like> like = likeRepository.findByPostIdAndUserUsername(postId, username);
        if(like.isEmpty()) {
            return createNewLike(request, postId, username);
        } else if(request.isLike() != like.get().isPositive()) {
            return switchLike(request, postId, like.get());
        } else {
            return like.get();
        }
    }

    private Like switchLike(LikeRequest request, Long postId, Like toUpdate) {
        toUpdate.setPositive(request.isLike());
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        post.setLikeCount(request.isLike() ? post.getLikeCount()+1 : post.getLikeCount()-1);
        post.setDislikeCount(request.isLike() ? post.getDislikeCount()-1 : post.getDislikeCount()+1);
        postRepository.save(post);
        return likeRepository.save(toUpdate);
    }

    private Like createNewLike(LikeRequest request, Long postId, String username) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        post.setLikeCount(request.isLike() ? post.getLikeCount()+1 : post.getLikeCount());
        post.setDislikeCount(request.isLike() ? post.getDislikeCount() : post.getDislikeCount()+1);
        Like newLike = new Like();
        newLike.setPost(post);
        newLike.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Not such user!"))
        );
        newLike.setPositive(request.isLike());
        postRepository.save(post);
        return likeRepository.save(newLike);
    }

    @Transactional
    public void unlikePost(Long postId, String username) {
        Like like = likeRepository.findByPostIdAndUserUsername(postId, username)
                .orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        post.setLikeCount(like.isPositive() ? post.getLikeCount()-1 : post.getLikeCount());
        post.setDislikeCount(like.isPositive() ? post.getDislikeCount() : post.getDislikeCount()-1);
        postRepository.save(post);
        likeRepository.delete(like);
    }
}
