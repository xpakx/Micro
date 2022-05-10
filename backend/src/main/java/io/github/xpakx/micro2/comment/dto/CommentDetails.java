package io.github.xpakx.micro2.comment.dto;

import io.github.xpakx.micro2.user.dto.UserMin;

import java.time.LocalDateTime;

public interface CommentDetails {
    Long getId();
    String getContent();
    UserMin getUser();
    LocalDateTime getCreatedAt();
    boolean isEdited();
}
