package io.github.xpakx.micro2.post.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PostTooOldToEditException extends RuntimeException {
    public PostTooOldToEditException() {
        super("Post is older than 24 hours!");
    }
}
