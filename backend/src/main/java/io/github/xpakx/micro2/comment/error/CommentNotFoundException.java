package io.github.xpakx.micro2.comment.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }
    public CommentNotFoundException() {
        super("Comment with given ID doesn't exist!");
    }
}
