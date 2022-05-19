package io.github.xpakx.micro2.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.github.xpakx.micro2.user.dto.UserMin;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface CommentDetails {
    Long getId();

    @Value("#{target.deletedByUser ? '[comment deleted by author]' : target.content}")
    String getContent();
    UserMin getUser();

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    LocalDateTime getCreatedAt();
    boolean isEdited();
    boolean isDeletedByUser();
    Integer getLikeCount();
    Integer getDislikeCount();
}
