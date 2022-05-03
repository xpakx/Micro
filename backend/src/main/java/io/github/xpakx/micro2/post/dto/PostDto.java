package io.github.xpakx.micro2.post.dto;

import io.github.xpakx.micro2.post.Post;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostDto {
    private Long id;
    private String message;
    private String username;

    public static PostDto fromPost(Post post) {
        PostDto transformed = new PostDto();
        transformed.setId(post.getId());
        transformed.setMessage(post.getContent());
        transformed.setUsername(post.getUser().getUsername());
        return  transformed;
    }
}
