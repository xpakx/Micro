package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.comment.dto.CommentDetails;
import io.github.xpakx.micro2.post.dto.PostDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PostRepositoryCustom {
    Page<PostDetails> getPostsWithMostResponsesAfterDate(LocalDateTime date, PageRequest pageable);
}
