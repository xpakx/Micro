package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.comment.dto.CommentCount;
import io.github.xpakx.micro2.comment.dto.CommentDetails;
import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.post.dto.PostUserInfo;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public Map<Long, PostUserInfo> getUserInfoMapForPostIds(List<Long> ids, String username) {
        Query userIdQuery = this.entityManager.createQuery(
                "SELECT u.id FROM UserAccount u WHERE u.username = ?1"
        );
        userIdQuery.setParameter(1, username);
        Long userId = (long) userIdQuery.getSingleResult();
        List<PostUserInfo> list = getUserInfoForEveryPost(ids, userId);
        Map<Long, PostUserInfo> result = new HashMap<>();
        for(PostUserInfo userInfo : list) {
            result.put(userInfo.getPostId(), userInfo);
        }
        return result;
    }

    private List<PostUserInfo> getUserInfoForEveryPost(List<Long> ids, Long userId) {
        Query query = this.entityManager.createNativeQuery(
                "SELECT CASE WHEN f.id IS NULL THEN v.post_id ELSE f.post_id END AS post_id, " +
                        "CASE WHEN v.positive = true THEN true ELSE false END AS liked, " +
                        "CASE WHEN v.positive = false THEN true ELSE false END AS disliked, " +
                        "CASE WHEN f.id IS NULL THEN false ELSE true END AS fav " +
                        "FROM fav_post f " +
                        "FULL OUTER JOIN vote v ON v.post_id = f.post_id AND f.user_id = v.user_id " +
                        "WHERE " +
                        "CASE WHEN f.id IS NULL THEN v.user_id = ?2 ELSE f.user_id = ?2 END " +
                        "AND CASE WHEN f.id IS NULL THEN v.post_id IN ?1 ELSE f.post_id IN ?1 END "
        );
        query.setParameter(1, ids);
        query.setParameter(2, userId);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(this::mapToUserInfo)
                .collect(Collectors.toList());
    }
    private PostUserInfo mapToUserInfo(Object[] object) {
        PostUserInfo post = new PostUserInfo();
        post.setPostId(((BigInteger) object[0]).longValue());
        System.out.print("id: "+post.getPostId());
        post.setLiked((Boolean) object[1]);
        System.out.print("l: "+post.isLiked());
        post.setDisliked((Boolean) object[2]);
        System.out.print("d: "+post.isDisliked());
        post.setFav((Boolean) object[3]);
        System.out.print("f: "+post.isFav());
        System.out.println();
        return post;
    }
}
