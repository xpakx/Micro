package io.github.xpakx.micro2.administration.dto;

import io.github.xpakx.micro2.comment.dto.CommentWithOnlyId;
import io.github.xpakx.micro2.post.dto.PostWithOnlyId;
import io.github.xpakx.micro2.user.dto.UserMin;

import java.time.LocalDateTime;

public interface ModerationDetails {
    Long getId();

    boolean isModerated();
    boolean isDeleted();
    LocalDateTime getCreatedAt();
    LocalDateTime getModeratedAt();
    String getReason();

    PostWithOnlyId getPost();
    CommentWithOnlyId getComment();
    UserMin getAuthor();
    UserMin getModerator();
}
