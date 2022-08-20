package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentCount;
import io.github.xpakx.micro2.comment.dto.CommentDetails;
import io.github.xpakx.micro2.comment.dto.CommentUserInfo;
import io.github.xpakx.micro2.comment.dto.CommentWithUserData;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final EntityManager entityManager;
    private final ProjectionFactory projectionFactory;

    @Override
    public Map<Long, Page<CommentWithUserData>> getCommentMapForPostIds(List<Long> ids) {
        Map<Long, List<CommentDetails>> mapOfComments = get2CommentsForEveryPost(ids).stream()
                .collect(
                        Collectors.groupingBy((c) -> c.getPost().getId(),
                                Collectors.mapping((r)->projectionFactory.createProjection(CommentDetails.class, r),
                                        Collectors.toList()))
                );
        Map<Long, Integer> mapOfCommentCounts = getCountMapForPostIds(ids);
        Map<Long, Page<CommentWithUserData>> result = new HashMap<>();
        Pageable pageable = PageRequest.of(0, 2);
        for(Long key: mapOfComments.keySet()) {
            List<CommentDetails> value = mapOfComments.get(key);
            result.put(
                    key,
                    new PageImpl<>(value.stream().map(CommentWithUserData::of).collect(Collectors.toList()), pageable, mapOfCommentCounts.get(key))
            );
        }
        return result;
    }

    private List<Comment> get2CommentsForEveryPost(List<Long> ids) {
        Query query = this.entityManager.createNativeQuery(
                "SELECT c.id AS id, c.content AS content, c.created_at AS created_at, c.edited AS edited, c.deleted_by_user AS deleted_by_user, " +
                        "c.deleted_by_post_author AS deleted_by_post_author, c.like_count AS like_count, c.dislike_count AS dislike_count, " +
                        "u.username AS user_username, u.gender AS user_gender, u.avatar_url AS user_avatar_url, u.confirmed AS user_confirmed, " +
                        "c.post_id AS post_id, c.attachment_url AS attachment_url, c.deleted_by_moderator AS deleted_by_moderator " +
                        "FROM comment c " +
                        "LEFT JOIN user_account u ON c.user_id = u.id " +
                        "WHERE c.id IN (SELECT c2.id FROM comment c2 WHERE c2.post_id = c.post_id ORDER BY c2.created_at DESC LIMIT 2) " +
                        "AND c.post_id IN ?1 ORDER BY c.created_at DESC"
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
        comment.setAttachmentUrl((String) object[13]);
        comment.setDeletedByModerator((boolean) object[14]);
        return comment;
    }

    public Map<Long, Integer> getCountMapForPostIds(List<Long> ids) {
        List<CommentCount> list = getCommentCountForPosts(ids);
        Map<Long, Integer> result = new HashMap<>();
        for(CommentCount commentCount : list) {
            result.put(commentCount.getPostId(), commentCount.getCommentCount());
        }
        return result;
    }

    private List<CommentCount> getCommentCountForPosts(List<Long> ids) {
        Query query = this.entityManager.createNativeQuery(
                "SELECT c.post_id AS id, count(c.id) AS comment_count " +
                        "FROM comment c " +
                        "WHERE c.post_id IN ?1 " +
                        "GROUP BY c.post_id "
        );
        query.setParameter(1, ids);
        List<Object[]> results = query.getResultList();
        return results.stream()
                .map(this::mapToCommentCount)
                .collect(Collectors.toList());
    }

    private CommentCount mapToCommentCount(Object[] object) {
        CommentCount commentCount = new CommentCount();
        commentCount.setPostId(((BigInteger) object[0]).longValue());
        commentCount.setCommentCount(((BigInteger) object[1]).intValue());
        return commentCount;
    }

    @Override
    public Map<Long, CommentUserInfo> getUserInfoMapForCommentIds(List<Long> ids, String username) {
        Query userIdQuery = this.entityManager.createQuery(
                "SELECT u.id FROM UserAccount u WHERE u.username = ?1"
        );
        userIdQuery.setParameter(1, username);
        Long userId = (long) userIdQuery.getSingleResult();
        List<CommentUserInfo> list = getUserInfoForEveryComment(ids, userId);
        Map<Long, CommentUserInfo> result = new HashMap<>();
        for(CommentUserInfo userInfo : list) {
            result.put(userInfo.getCommentId(), userInfo);
        }
        return result;
    }

    private List<CommentUserInfo> getUserInfoForEveryComment(List<Long> ids, Long userId) {
        Query query = this.entityManager.createNativeQuery(
                "SELECT v.comment_id AS comment_id, " +
                        "v.positive AS liked, " +
                        "NOT(v.positive) AS disliked " +
                        "FROM vote v " +
                        "WHERE " +
                        "v.user_id = ?2 " +
                        "AND v.comment_id IN ?1  "
        );
        query.setParameter(1, ids);
        query.setParameter(2, userId);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(this::mapToUserInfo)
                .collect(Collectors.toList());
    }

    private CommentUserInfo mapToUserInfo(Object[] object) {
        CommentUserInfo comment = new CommentUserInfo();
        comment.setCommentId(((BigInteger) object[0]).longValue());
        comment.setLiked((Boolean) object[1]);
        comment.setDisliked((Boolean) object[2]);
        return comment;
    }
}
