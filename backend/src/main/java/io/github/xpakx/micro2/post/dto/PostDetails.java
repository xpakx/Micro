package io.github.xpakx.micro2.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.github.xpakx.micro2.user.dto.UserMin;

import java.time.LocalDateTime;

public interface PostDetails {
    Long getId();
    String getContent();
    UserMin getUser();

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    LocalDateTime getCreatedAt();
    boolean isEdited();
    Integer getLikeCount();
    Integer getDislikeCount();
}
