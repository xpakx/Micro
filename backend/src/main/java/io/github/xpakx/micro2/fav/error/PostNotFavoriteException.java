package io.github.xpakx.micro2.fav.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PostNotFavoriteException extends RuntimeException {
    public PostNotFavoriteException(String message) {
        super(message);
    }
    public PostNotFavoriteException() {
        super("Post is not added fo favorites!");
    }
}