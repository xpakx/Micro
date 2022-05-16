package io.github.xpakx.micro2.like;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    long countByPostId(Long postId);
    long countByCommentId(Long commentId);
    List<Like> findByPostId(Long postId);
    List<Like> findByCommentId(Long commentId);

    Optional<Like> findByPostIdAndUserUsername(Long postId, String username);
    <T> Optional<T> findProjectedByPostIdAndUserUsername(Long postId, String username, Class<T> type);
}
