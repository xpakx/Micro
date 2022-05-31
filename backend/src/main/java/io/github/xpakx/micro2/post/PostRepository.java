package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {
    Optional<Post> findByIdAndUserUsername(Long id, String username);
    Page<PostDetails> findAllBy(Pageable pageable);
    Page<PostDetails> getAllByUserUsername(String username, Pageable pageable);
    Page<PostDetails> findAllByTagsName(String name, Pageable pageable);
    Optional<PostDetails> findProjectedById(Long id);

    Optional<Post> findByCommentsId(Long id);
    Page<PostDetails> findAllByCreatedAtAfter(LocalDateTime createdAt, Pageable pageable);
    Page<PostDetails> findAllByFavoriteUserUsername(String username, Pageable pageable);

    @Query(value = "SELECT * FROM post " +
            "ORDER BY (SELECT count(post_id) FROM post LEFT JOIN comment ON post.id = post_id " +
            "WHERE post_id = post.id AND post.created_at > ?1) DESC " +
            "LIMIT ?#{#pageable.getPageSize()} OFFSET ?#{#pageable.getOffset()}",
            nativeQuery = true, countQuery = "SELECT COUNT(*) FROM post WHERE post.created_at > :date")
    Page<PostDetails> getPostsWithMostResponsesAfterDate(LocalDateTime date, PageRequest pageable);
}
