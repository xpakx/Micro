package io.github.xpakx.micro2.like;

import io.github.xpakx.micro2.like.dto.CommentLikeDto;
import io.github.xpakx.micro2.like.dto.LikeDetails;
import io.github.xpakx.micro2.like.dto.LikeRequest;
import io.github.xpakx.micro2.like.dto.UnlikeDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/comments/{commentId}/like")
public class CommentLikeController {
    private final CommentLikeService service;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentLikeDto> likeComment(@RequestBody LikeRequest request, @PathVariable Long commentId, Principal principal) {
        return new ResponseEntity<>(
                service.likeComment(request, commentId, principal.getName()),
                HttpStatus.CREATED
        );
    }
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnlikeDto> unlikePost(@PathVariable Long commentId, Principal principal) {
        return new ResponseEntity<>(service.unlikeComment(commentId, principal.getName()), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LikeDetails> getLike(@PathVariable Long commentId, Principal principal) {
        return new ResponseEntity<>(
                service.getLike(commentId, principal.getName()),
                HttpStatus.OK
        );
    }
}
