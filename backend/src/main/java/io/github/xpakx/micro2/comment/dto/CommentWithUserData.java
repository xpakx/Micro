package io.github.xpakx.micro2.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentWithUserData {
    private CommentDetails comment;
    boolean liked = false;
    boolean disliked = false;

    public static CommentWithUserData of(CommentDetails comment) {
        CommentWithUserData newComment = new CommentWithUserData();
        newComment.setComment(comment);
        return newComment;
    }

    public static CommentWithUserData of(CommentDetails comment, CommentUserInfo info) {
        if(info == null) {
            return of(comment);
        }
        CommentWithUserData newComment = new CommentWithUserData();
        newComment.setComment(comment);
        newComment.setLiked(info.isLiked());
        newComment.setDisliked(info.isDisliked());
        return newComment;
    }
}
