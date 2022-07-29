package io.github.xpakx.micro2.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private String message;
    private String encodedAttachment;
}
