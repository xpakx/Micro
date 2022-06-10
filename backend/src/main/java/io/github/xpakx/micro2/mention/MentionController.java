package io.github.xpakx.micro2.mention;

import io.github.xpakx.micro2.mention.dto.MentionCountResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class MentionController {
    private final MentionService service;

    @GetMapping("/mentions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MentionCountResponse> getMentionCount(Principal principal) {
        return new ResponseEntity<MentionCountResponse>(
                service.getMentionCount(principal.getName()),
                HttpStatus.OK
        );
    }
}
