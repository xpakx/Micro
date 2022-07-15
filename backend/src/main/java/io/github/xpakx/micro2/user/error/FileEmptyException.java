package io.github.xpakx.micro2.user.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileEmptyException extends RuntimeException {
    public FileEmptyException(String message) {
        super(message);
    }

    public FileEmptyException() {
        super("File can't be empty!");
    }
}
