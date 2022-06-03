package io.github.xpakx.micro2.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUserInfo {
    Long postId;
    boolean liked;
    boolean disliked;
    boolean fav;
}
