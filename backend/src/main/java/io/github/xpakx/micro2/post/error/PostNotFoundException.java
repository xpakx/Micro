package io.github.xpakx.micro2.post.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
    public PostNotFoundException() {
        super("Post with given ID doesn't exist!");
    }
}
