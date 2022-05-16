package io.github.xpakx.micro2.like;

import io.github.xpakx.micro2.like.dto.CommentLikeDto;
import io.github.xpakx.micro2.like.dto.LikeDetails;
import io.github.xpakx.micro2.like.dto.LikeRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user/{username}/comments/{commentId}/like")
public class CommentLikeController {
    private final CommentLikeService service;

    @PostMapping
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<CommentLikeDto> likeComment(@RequestBody LikeRequest request, @PathVariable String username, @PathVariable Long commentId) {
        return new ResponseEntity<>(
                service.likeComment(request, commentId, username),
                HttpStatus.CREATED
        );
    }
    @DeleteMapping
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<?> unlikePost(@PathVariable String username, @PathVariable Long commentId) {
        service.unlikeComment(commentId, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<LikeDetails> getLike(@PathVariable String username, @PathVariable Long commentId) {
        return new ResponseEntity<>(
                service.getLike(commentId, username),
                HttpStatus.OK
        );
    }
}
