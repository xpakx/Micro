package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentUserInfo;
import io.github.xpakx.micro2.comment.dto.CommentWithUserData;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentRepositoryCustom {
    Map<Long, Page<CommentWithUserData>> getCommentMapForPostIds(List<Long> ids);
    Map<Long, CommentUserInfo> getUserInfoMapForCommentIds(List<Long> ids, String username);
}
