package io.github.xpakx.micro2.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUserInfo {
    Long commentId;
    boolean liked;
    boolean disliked;
}
