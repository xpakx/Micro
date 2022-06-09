package io.github.xpakx.micro2.follows;

import io.github.xpakx.micro2.follows.dto.FollowRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/follows/users/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> unfollowUser(@PathVariable String username, Principal principal) {
        service.unfollowUser(username, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/follows/tags/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> unfollowTag(@PathVariable String name, Principal principal) {
        service.unfollowTag(name, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
