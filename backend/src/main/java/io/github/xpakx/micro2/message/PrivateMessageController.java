package io.github.xpakx.micro2.message;

import io.github.xpakx.micro2.message.dto.MessageCountResponse;
import io.github.xpakx.micro2.message.dto.MessageDto;
import io.github.xpakx.micro2.message.dto.MessageRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/messages/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageCountResponse> getMessageCount(Principal principal) {
        return new ResponseEntity<MessageCountResponse>(
                service.getMessageCount(principal.getName()),
                HttpStatus.OK
        );
    }
}
