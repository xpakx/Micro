package io.github.xpakx.micro2.message.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException  extends RuntimeException {
    public MessageNotFoundException(String message) {
        super(message);
    }
    public MessageNotFoundException() {
        super("Message not found!");
    }
}
