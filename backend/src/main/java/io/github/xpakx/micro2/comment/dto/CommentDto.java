package io.github.xpakx.micro2.comment.dto;

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
    private LocalDateTime createdAt;
    private Integer likeCount;
    private Integer dislikeCount;

    public static CommentDto fromComment(Comment comm) {
        CommentDto transformed = new CommentDto();
        transformed.setId(comm.getId());
        transformed.setMessage(comm.getContent());
        transformed.setUsername(comm.getUser().getUsername());
        transformed.setCreatedAt(comm.getCreatedAt());
        transformed.setLikeCount(comm.getLikeCount());
        transformed.setDislikeCount(comm.getDislikeCount());
        return transformed;
    }
}
