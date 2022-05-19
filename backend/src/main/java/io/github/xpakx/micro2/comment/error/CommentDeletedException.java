package io.github.xpakx.micro2.comment.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommentDeletedException extends RuntimeException {
    public CommentDeletedException() {
        super("Comment is deleted!");
    }
}
