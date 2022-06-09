package io.github.xpakx.micro2.follows.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyFollowedException extends RuntimeException {
    public AlreadyFollowedException(String message) {
        super(message);
    }
    public AlreadyFollowedException() {
        super("Already followed!");
    }
}
