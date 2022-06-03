package io.github.xpakx.micro2.post.dto;

import lombok.Getter;
import lombok.Setter;

import io.github.xpakx.micro2.comment.dto.CommentDetails;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class PostWithCommentsAndUserInfo {
    private PostDetails post;
    private Page<CommentDetails> comments;
    boolean liked = false;
    boolean disliked = false;
    boolean fav = false;

    public static PostWithCommentsAndUserInfo of(PostDetails post, Page<CommentDetails> comments, PostUserInfo info) {
        PostWithCommentsAndUserInfo newPost = new PostWithCommentsAndUserInfo();
        newPost.setPost(post);
        newPost.setComments(comments);
        if(info != null) {
            newPost.setLiked(info.isLiked());
            newPost.setDisliked(info.isDisliked());
            newPost.setFav(info.isFav());
        }
        return newPost;
    }
}
