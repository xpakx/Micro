package io.github.xpakx.micro2.like;

import io.github.xpakx.micro2.like.dto.LikeRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user/{username}")
public class PostLikeController {
    private final PostLikeService service;

    @PostMapping("/posts/{postId}/like")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Like> likePost(@RequestBody LikeRequest request, @PathVariable String username, @PathVariable Long postId) {
        return new ResponseEntity<>(
                service.likePost(request, postId, username),
                HttpStatus.CREATED
        );
    }
    @DeleteMapping("/posts/{postId}/like")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<?> unlikePost(@PathVariable String username, @PathVariable Long postId) {
        service.unlikePost(postId, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

