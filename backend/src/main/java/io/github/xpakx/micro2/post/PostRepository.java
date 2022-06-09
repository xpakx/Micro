package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long>, PostRepositoryCustom {
    Optional<Post> findByIdAndUserUsername(Long id, String username);
    Page<PostDetails> findAllBy(Pageable pageable);
    Page<PostDetails> getAllByUserUsername(String username, Pageable pageable);
    Page<PostDetails> findAllByTagsName(String name, Pageable pageable);
    Optional<PostDetails> findProjectedById(Long id);

    Optional<Post> findByCommentsId(Long id);
    Page<PostDetails> findAllByCreatedAtAfter(LocalDateTime createdAt, Pageable pageable);
    Page<PostDetails> findAllByFavoriteUserUsername(String username, Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.user.id IN (SELECT us.id FROM UserFollows u LEFT JOIN u.users us WHERE u.user.username = :username)" +
            "ORDER BY p.createdAt DESC")
    Page<PostDetails> findAllByFollowedUsers(String username, Pageable pageable);

    @Query(value = "SELECT p FROM Post p LEFT JOIN p.tags t2  " +
            "WHERE t2.id IN (SELECT t1.id FROM UserFollows u LEFT JOIN u.tags t1 WHERE u.user.username = :name) " +
            "ORDER BY p.createdAt DESC")
    Page<PostDetails> findAllFromFollowedTags(String name, Pageable pageable);
}
