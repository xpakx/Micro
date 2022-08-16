package io.github.xpakx.micro2.administration.dto;

import io.github.xpakx.micro2.post.dto.PostWithOnlyId;

public interface CommentWithIdAndContent {
    Long getId();
    String getContent();
    PostWithOnlyId getPost();
}
