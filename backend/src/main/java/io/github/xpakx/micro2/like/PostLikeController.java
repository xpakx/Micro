package io.github.xpakx.micro2.like;

import io.github.xpakx.micro2.like.dto.LikeRequest;
import io.github.xpakx.micro2.like.dto.LikeDetails;
import io.github.xpakx.micro2.like.dto.PostLikeDto;
import io.github.xpakx.micro2.like.dto.UnlikeDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/posts/{postId}/like")
public class PostLikeController {
    private final PostLikeService service;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostLikeDto> likePost(@RequestBody LikeRequest request, @PathVariable Long postId, Principal principal) {
        return new ResponseEntity<>(
                service.likePost(request, postId, principal.getName()),
                HttpStatus.CREATED
        );
    }
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnlikeDto> unlikePost(@PathVariable Long postId, Principal principal) {
        return new ResponseEntity<>(service.unlikePost(postId, principal.getName()), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LikeDetails> getLike(@PathVariable Long postId, Principal principal) {
        return new ResponseEntity<>(
                service.getLike(postId, principal.getName()),
                HttpStatus.OK
        );
    }
}
