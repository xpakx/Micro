package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final EntityManager entityManager;
    private final ProjectionFactory projectionFactory;

    @Override
    public Page<PostDetails> getPostsWithMostResponsesAfterDate(LocalDateTime date, PageRequest pageable) {
        List<PostDetails> postResults = getActivePosts(date, pageable);
        int count = getPostCount(date);
        return new PageImpl<PostDetails>(postResults, pageable, count);
    }

    private int getPostCount(LocalDateTime date) {
        Query countQuery = this.entityManager.createQuery(
                "SELECT COUNT(*) FROM Post p WHERE p.createdAt > ?1"
        );
        countQuery.setParameter(1, date);
        long countResult = (long) countQuery.getSingleResult();
        int count = (int) countResult;
        return count;
    }

    private List<PostDetails> getActivePosts(LocalDateTime date, PageRequest pageable) {
        Query query = this.entityManager.createNativeQuery(
                "SELECT post.id AS id, post.content AS content, post.created_at AS created_at, " +
                        "post.like_count AS like_count, post.dislike_count AS dislike_count, " +
                        "u.username AS user_username, u.gender AS user_gender, u.avatar_url AS user_avatar_url, u.confirmed AS user_confirmed " +
                        "FROM post " +
                        "LEFT JOIN user_account u ON post.user_id = u.id " +
                        "WHERE post.created_at > ?1 " +
                        "ORDER BY (SELECT count(post_id) FROM comment WHERE post.id = comment.post_id) DESC " +
                        "LIMIT ?2 OFFSET ?3"
        );
        query.setParameter(1, date);
        query.setParameter(2, pageable.getPageSize());
        query.setParameter(3, pageable.getOffset());
        List<Object[]> results = query.getResultList();
        return results.stream()
                .map(this::mapToPost)
                .map((p) -> projectionFactory.createProjection(PostDetails.class, p))
                .collect(Collectors.toList());
    }

    private Post mapToPost(Object[] object) {
        Post post = new Post();
        post.setId(((BigInteger) object[0]).longValue());
        post.setContent((String) object[1]);
        post.setCreatedAt(((java.sql.Timestamp) object[2]).toLocalDateTime());
        post.setLikeCount((Integer) object[3]);
        post.setDislikeCount((Integer) object[4]);
        UserAccount user = new UserAccount();
        user.setUsername((String) object[5]);
        user.setGender((String) object[6]);
        user.setAvatarUrl((String) object[7]);
        user.setConfirmed((boolean) object[8]);
        post.setUser(user);
        return post;
    }
}
