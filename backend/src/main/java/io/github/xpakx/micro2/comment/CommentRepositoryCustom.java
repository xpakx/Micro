package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentDetails;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CommentRepositoryCustom {
    Map<Long, Page<CommentDetails>> getCommentMapForPostIds(List<Long> ids);
}
