package io.github.xpakx.micro2.follows;

import io.github.xpakx.micro2.follows.dto.FollowRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@AllArgsConstructor
@RestController
public class FollowController {
    private final FollowService service;

    @PostMapping("/follows/users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> followUser(@RequestBody FollowRequest request, Principal principal) {
        service.followUser(request.getName(), principal.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/follows/tags")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> followTag(@RequestBody FollowRequest request, Principal principal) {
        service.followTag(request.getName(), principal.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
