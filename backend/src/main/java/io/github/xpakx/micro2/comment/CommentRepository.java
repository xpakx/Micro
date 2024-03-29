package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentDetails;
import io.github.xpakx.micro2.comment.dto.CommentSearchDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Long>, CommentRepositoryCustom {
    Optional<Comment> findByIdAndUserUsername(Long id, String username);
    Page<CommentDetails> getAllByUserUsername(String username, Pageable pageable);
    Page<CommentDetails> getAllByPostId(Long postId, Pageable pageable);
    Page<CommentDetails> findAllBy(Pageable pageable);
    boolean existsByPostIdAndCreatedAtIsGreaterThan(Long postId, LocalDateTime createdAt);
    Optional<CommentDetails> findProjectedById(Long id);
    Page<CommentSearchDetails> findByContentIsContainingIgnoreCaseAndDeletedByUserIsFalseAndDeletedByPostAuthorIsFalseAndDeletedByModeratorIsFalse(String content, Pageable pageable);
}