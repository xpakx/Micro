package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostWithComments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

public interface PostRepositoryCustom {
    Page<PostWithComments> getPostsWithMostResponsesAfterDate(LocalDateTime date, PageRequest pageable);
}
