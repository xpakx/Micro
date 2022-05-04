package io.github.xpakx.micro2.post.dto;

import io.github.xpakx.micro2.user.dto.UserMin;

import java.time.LocalDateTime;

public interface PostDetails {
    Long getId();
    String getContent();
    UserMin getUser();
    LocalDateTime getCreatedAt();
    boolean isEdited();
}
