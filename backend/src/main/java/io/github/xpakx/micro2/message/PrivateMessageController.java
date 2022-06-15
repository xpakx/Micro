package io.github.xpakx.micro2.message;

import io.github.xpakx.micro2.message.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

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

    @GetMapping("/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<MessageMin>> getMessageList(@RequestParam("page") Optional<Integer> page, Principal principal) {
        return new ResponseEntity<Page<MessageMin>>(
                service.getMessageList(principal.getName(), page.orElse(0)),
                HttpStatus.OK
        );
    }

    @GetMapping("/messages/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDetails> getSingleComment(@PathVariable Long messageId, Principal principal)
    {
        return new ResponseEntity<MessageDetails>(
                service.getSingleMessage(messageId, principal.getName()), HttpStatus.OK
        );
    }
}
