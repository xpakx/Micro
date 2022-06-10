package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.post.dto.PostUserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostRepositoryCustom {
    Page<PostDetails> getPostsWithMostResponsesAfterDate(LocalDateTime date, PageRequest pageable);
    Map<Long, PostUserInfo> getUserInfoMapForPostIds(List<Long> ids, String username);
    Optional<PostUserInfo> getUserInfoForPostId(Long postId, String username);
    List<PostDetails> get2RandomHotPosts(LocalDateTime date);
    Page<PostDetails> findAllByFollowedUsers(String username, PageRequest pageable);
    Page<PostDetails> findAllFromFollowedTags(String username, PageRequest pageable);
}
