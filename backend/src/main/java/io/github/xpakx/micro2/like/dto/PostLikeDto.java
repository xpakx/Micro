package io.github.xpakx.micro2.like.dto;

import io.github.xpakx.micro2.like.Like;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostLikeDto {
    private Long id;
    private Long postId;
    private boolean voted;
    private boolean positive;

    public static PostLikeDto from(Like like) {
        PostLikeDto transformed = new PostLikeDto();
        transformed.setVoted(true);
        transformed.setPositive(like.isPositive());
        transformed.setId(like.getId());
        transformed.setPostId(like.getPost().getId());
        return transformed;
    }

    public static PostLikeDto empty() {
        PostLikeDto emptyLike = new PostLikeDto();
        emptyLike.setVoted(false);
        emptyLike.setPositive(false);
        emptyLike.setId(null);
        emptyLike.setPostId(null);
        return emptyLike;
    }
}
