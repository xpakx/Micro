package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.ModerationRequest;
import io.github.xpakx.micro2.administration.dto.RoleRequest;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ModerationController {
    private final ModerationService service;

    @DeleteMapping("moderation/post/{postId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_MOD')")
    public ResponseEntity<Moderation> deleteRole(@RequestBody ModerationRequest request, @PathVariable Long postId, Principal principal) {
        return new ResponseEntity<>(
                service.moderatePost(request, postId, principal.getName()),
                HttpStatus.OK
        );
    }
}
