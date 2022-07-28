package io.github.xpakx.micro2.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
    private String message;
    private String encodedAttachment;
}
