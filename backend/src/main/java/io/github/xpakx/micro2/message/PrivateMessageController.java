package io.github.xpakx.micro2.message;

import io.github.xpakx.micro2.message.dto.MessageDto;
import io.github.xpakx.micro2.message.dto.MessageRequest;
import io.github.xpakx.micro2.post.dto.PostDto;
import io.github.xpakx.micro2.post.dto.PostRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class PrivateMessageController {
    private final PrivateMessageService service;

    @PostMapping("/users/{recipient}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDto> sendMessage(@RequestBody MessageRequest request, @PathVariable String recipient, Principal principal) {
        return new ResponseEntity<>(
                service.sendMessage(request, principal.getName(), recipient),
                HttpStatus.CREATED
        );
    }
}
