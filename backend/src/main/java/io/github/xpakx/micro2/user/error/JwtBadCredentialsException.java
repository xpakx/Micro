package io.github.xpakx.micro2.user.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtBadCredentialsException extends RuntimeException {
    public JwtBadCredentialsException(String message) {
        super(message);
    }
}
