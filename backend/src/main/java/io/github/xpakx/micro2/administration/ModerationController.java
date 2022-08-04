package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.ModerationRequest;
import io.github.xpakx.micro2.administration.dto.RoleRequest;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ModerationController {
    private final ModerationService service;

    @PostMapping("moderation/post/{postId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_MOD')")
    public ResponseEntity<Moderation> moderatePost(@RequestBody ModerationRequest request, @PathVariable Long postId, Principal principal) {
        return new ResponseEntity<>(
                service.moderatePost(request, postId, principal.getName()),
                HttpStatus.OK
        );
    }

    @PostMapping("moderation/comment/{commentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_MOD')")
    public ResponseEntity<Moderation> moderateComment(@RequestBody ModerationRequest request, @PathVariable Long commentId, Principal principal) {
        return new ResponseEntity<>(
                service.moderateComment(request, commentId, principal.getName()),
                HttpStatus.OK
        );
    }

    @PostMapping("moderation/{modId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_MOD')")
    public ResponseEntity<Moderation> moderate(@RequestBody ModerationRequest request, @PathVariable Long modId, Principal principal) {
        return new ResponseEntity<>(
                service.moderate(request, modId, principal.getName()),
                HttpStatus.OK
        );
    }
}
