package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.ModerationDetails;
import io.github.xpakx.micro2.administration.dto.ModerationRequest;
import io.github.xpakx.micro2.administration.dto.RoleRequest;
import io.github.xpakx.micro2.comment.dto.CommentDto;
import io.github.xpakx.micro2.comment.dto.CommentRequest;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

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

    @GetMapping("moderation/all")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_MOD')")
    public ResponseEntity<Page<ModerationDetails>> getAll(@RequestParam("page") Optional<Integer> page) {
        return new ResponseEntity<>(
                service.getAll(page.orElse(0)),
                HttpStatus.OK
        );
    }

    @GetMapping("moderation/unmoderated")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_MOD')")
    public ResponseEntity<Page<ModerationDetails>> getUnmoderated(@RequestParam("page") Optional<Integer> page) {
        return new ResponseEntity<>(
                service.getUnmoderated(page.orElse(0)),
                HttpStatus.OK
        );
    }

    @GetMapping("/moderation/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ModerationDetails>> getMyModerated(@RequestParam("page") Optional<Integer> page, Principal principal) {
        return new ResponseEntity<>(
                service.getForUser(page.orElse(0), principal.getName()),
                HttpStatus.OK
        );
    }

    @GetMapping("/moderation/my/reports")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ModerationDetails>> getMyReports(@RequestParam("page") Optional<Integer> page, Principal principal) {
        return new ResponseEntity<>(
                service.getUserReports(page.orElse(0), principal.getName()),
                HttpStatus.OK
        );
    }
}
