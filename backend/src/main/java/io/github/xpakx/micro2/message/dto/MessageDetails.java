package io.github.xpakx.micro2.message.dto;

import io.github.xpakx.micro2.user.dto.UserMin;

import java.time.LocalDateTime;

public interface MessageDetails {
    Long getId();
    UserMin getSender();
    boolean isRead();
    String getContent();
    LocalDateTime getCreatedAt();
}
