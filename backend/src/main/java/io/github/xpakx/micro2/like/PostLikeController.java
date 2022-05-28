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

@RestController
@AllArgsConstructor
@RequestMapping("/user/{username}/posts/{postId}/like")
public class PostLikeController {
    private final PostLikeService service;

    @PostMapping
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<PostLikeDto> likePost(@RequestBody LikeRequest request, @PathVariable String username, @PathVariable Long postId) {
        return new ResponseEntity<>(
                service.likePost(request, postId, username),
                HttpStatus.CREATED
        );
    }
    @DeleteMapping
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<UnlikeDto> unlikePost(@PathVariable String username, @PathVariable Long postId) {
        return new ResponseEntity<>(service.unlikePost(postId, username), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<LikeDetails> getLike(@PathVariable String username, @PathVariable Long postId) {
        return new ResponseEntity<>(
                service.getLike(postId, username),
                HttpStatus.OK
        );
    }
}
