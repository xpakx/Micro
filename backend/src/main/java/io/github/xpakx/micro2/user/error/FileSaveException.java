package io.github.xpakx.micro2.user.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileSaveException extends RuntimeException {
    public FileSaveException(String message) {
        super(message);
    }

    public FileSaveException() {
        super("File couldn't be save!");
    }
}
