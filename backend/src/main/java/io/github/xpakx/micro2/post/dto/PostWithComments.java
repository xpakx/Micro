package io.github.xpakx.micro2.post.dto;

import io.github.xpakx.micro2.comment.dto.CommentDetails;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class PostWithComments {
    private PostDetails post;
    private Page<CommentDetails> comments;
    boolean liked = false;
    boolean disliked = false;
    boolean fav = false;

    public static PostWithComments of(PostDetails post, Page<CommentDetails> comments) {
        PostWithComments newPost = new PostWithComments();
        newPost.setPost(post);
        newPost.setComments(comments);
        return newPost;
    }

    public static PostWithComments of(PostDetails post, Page<CommentDetails> comments, PostUserInfo info) {
        if(info == null) {
            return of(post, comments);
        }
        PostWithComments newPost = new PostWithComments();
        newPost.setPost(post);
        newPost.setComments(comments);
        newPost.setLiked(info.isLiked());
        newPost.setDisliked(info.isDisliked());
        newPost.setFav(info.isFav());
        return newPost;
    }
}
