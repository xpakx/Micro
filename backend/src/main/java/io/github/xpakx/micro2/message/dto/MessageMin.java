package io.github.xpakx.micro2.message.dto;

import io.github.xpakx.micro2.user.dto.UserMin;

import java.time.LocalDateTime;

public interface MessageMin {
    Long getId();
    UserMin getSender();
    boolean isRead();
    LocalDateTime getCreatedAt();
}
