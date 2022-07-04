package io.github.xpakx.micro2.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.github.xpakx.micro2.post.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class PostDto {
    private Long id;
    private String message;
    private String username;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdAt;
    private Integer likeCount;
    private Integer dislikeCount;

    public static PostDto fromPost(Post post) {
        PostDto transformed = new PostDto();
        transformed.setId(post.getId());
        transformed.setMessage(post.getContent());
        transformed.setUsername(post.getUser().getUsername());
        transformed.setCreatedAt(post.getCreatedAt());
        transformed.setLikeCount(post.getLikeCount());
        transformed.setDislikeCount(post.getDislikeCount());
        return  transformed;
    }
}
