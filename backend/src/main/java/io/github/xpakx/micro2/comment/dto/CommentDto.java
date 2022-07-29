package io.github.xpakx.micro2.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.post.dto.PostDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String message;
    private String username;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdAt;
    private Integer likeCount;
    private Integer dislikeCount;
    private String attachmentUrl;

    public static CommentDto fromComment(Comment comm) {
        CommentDto transformed = new CommentDto();
        transformed.setId(comm.getId());
        transformed.setMessage(comm.getContent());
        transformed.setUsername(comm.getUser().getUsername());
        transformed.setCreatedAt(comm.getCreatedAt());
        transformed.setLikeCount(comm.getLikeCount());
        transformed.setDislikeCount(comm.getDislikeCount());
        transformed.setAttachmentUrl(comm.getAttachmentUrl());
        return transformed;
    }
}
