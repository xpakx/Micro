package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.comment.dto.CommentDetails;
import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.post.dto.PostWithComments;
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
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class PostListRepository {
    private final EntityManager entityManager;
    private final ProjectionFactory projectionFactory;

    public Page<PostWithComments> getPostsWithMostResponsesAfterDate(LocalDateTime date, PageRequest pageable) {
        List<PostDetails> postResults = getPosts(date, pageable);
        int count = getPostCount(date);
        Map<Long, List<CommentDetails>> mapOfComments = get2CommentsForEveryPost(
                postResults.stream()
                        .map(PostDetails::getId)
                        .collect(Collectors.toList())
        ).stream()
                .collect(
                        Collectors.groupingBy((c) -> c.getPost().getId(),
                                Collectors.mapping((r)->projectionFactory.createProjection(CommentDetails.class, r),
                                        Collectors.toList()))
                );
        List<PostWithComments> result = postResults.stream()
                .map(
                        (p) -> PostWithComments.of(p, new PageImpl<CommentDetails>(mapOfComments.get(p.getId())))
                ).collect(Collectors.toList());
        return new PageImpl<PostWithComments>(result, pageable, count);
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

    private List<PostDetails> getPosts(LocalDateTime date, PageRequest pageable) {
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


    public List<Comment> get2CommentsForEveryPost(List<Long> ids) {
        Query query = this.entityManager.createNativeQuery(
                "SELECT c.id AS id, c.content AS content, c.created_at AS created_at, c.edited AS edited, c.deleted_by_user AS deleted_by_user, " +
                        "c.deleted_by_post_author AS deleted_by_post_author, c.like_count AS like_count, c.dislike_count AS dislike_count, " +
                        "u.username AS user_username, u.gender AS user_gender, u.avatar_url AS user_avatar_url, u.confirmed AS user_confirmed, " +
                        "c.post_id AS post_id " +
                        "FROM comment c " +
                        "LEFT JOIN user_account u ON c.user_id = u.id " +
                        "WHERE c.id IN (SELECT c2.id FROM comment c2 WHERE c2.post_id = c.post_id ORDER BY c2.created_at DESC LIMIT 2) " +
                        "AND c.post_id IN ?1"
        );
        query.setParameter(1, ids);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(this::mapToComment)
                .collect(Collectors.toList());
    }

    private Comment mapToComment(Object[] object) {
        Comment comment = new Comment();
        comment.setId(((BigInteger) object[0]).longValue());
        comment.setContent((String) object[1]);
        comment.setCreatedAt(((java.sql.Timestamp) object[2]).toLocalDateTime());
        comment.setEdited((boolean) object[3]);
        comment.setDeletedByUser((boolean) object[4]);
        comment.setDeletedByPostAuthor((boolean) object[5]);
        comment.setLikeCount((Integer) object[6]);
        comment.setDislikeCount((Integer) object[7]);
        UserAccount user = new UserAccount();
        user.setUsername((String) object[8]);
        user.setGender((String) object[9]);
        user.setAvatarUrl((String) object[10]);
        user.setConfirmed((boolean) object[11]);
        comment.setUser(user);
        Post post = new Post();
        post.setId(((BigInteger) object[12]).longValue());
        comment.setPost(post);
        return comment;
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
