package io.github.xpakx.micro2.comment.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CannotDeleteCommentException extends RuntimeException {
    public CannotDeleteCommentException() {
        super("Cannot delete comment!");
    }
}
