package io.github.xpakx.micro2.mention.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MentionNotFoundException extends RuntimeException {
    public MentionNotFoundException(String message) {
        super(message);
    }
    public MentionNotFoundException() {
        super("Mention with given id not found!");
    }
}