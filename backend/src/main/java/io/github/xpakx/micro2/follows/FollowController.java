package io.github.xpakx.micro2.follows;

import io.github.xpakx.micro2.follows.dto.FollowRequest;
import io.github.xpakx.micro2.follows.dto.FollowedResponse;
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
        service.followUser(principal.getName(), request.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/follows/tags")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> followTag(@RequestBody FollowRequest request, Principal principal) {
        service.followTag(principal.getName(), request.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/follows/users/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> unfollowUser(@PathVariable String username, Principal principal) {
        service.unfollowUser(principal.getName(), username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/follows/tags/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> unfollowTag(@PathVariable String name, Principal principal) {
        service.unfollowTag(principal.getName(), name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/tags/{tagName}/followed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FollowedResponse> isTagFollowed(@PathVariable String tagName, Principal principal) {
        return new ResponseEntity<>(
                service.isTagFollowed(principal.getName(), tagName),
                HttpStatus.OK
        );
    }

    @GetMapping("/users/{username}/followed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FollowedResponse> isUserFollowed(@PathVariable String username, Principal principal) {
        return new ResponseEntity<>(
                service.isUserFollowed(principal.getName(), username),
                HttpStatus.OK
        );
    }
}
