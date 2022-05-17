package io.github.xpakx.micro2.like.dto;

import io.github.xpakx.micro2.like.Like;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentLikeDto {
    private Long id;
    private Long commentId;
    private boolean voted;
    private boolean positive;
    private Integer totalLikes;

    public static CommentLikeDto from(Like like) {
        CommentLikeDto transformed = new CommentLikeDto();
        transformed.setVoted(true);
        transformed.setPositive(like.isPositive());
        transformed.setId(like.getId());
        transformed.setCommentId(like.getComment().getId());
        transformed.setTotalLikes(0);
        return transformed;
    }

    public static CommentLikeDto from(Like like, Integer totalLikes) {
        CommentLikeDto transformed = new CommentLikeDto();
        transformed.setVoted(true);
        transformed.setPositive(like.isPositive());
        transformed.setId(like.getId());
        transformed.setCommentId(like.getComment().getId());
        transformed.setTotalLikes(totalLikes);
        return transformed;
    }

    public static CommentLikeDto empty() {
        CommentLikeDto emptyLike = new CommentLikeDto();
        emptyLike.setVoted(false);
        emptyLike.setPositive(false);
        emptyLike.setId(null);
        emptyLike.setCommentId(null);
        return emptyLike;
    }
}
