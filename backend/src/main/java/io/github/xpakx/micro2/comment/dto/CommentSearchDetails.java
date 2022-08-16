package io.github.xpakx.micro2.comment.dto;

import io.github.xpakx.micro2.post.dto.PostWithOnlyId;

public interface CommentSearchDetails extends CommentDetails {
    PostWithOnlyId getPost();
}
