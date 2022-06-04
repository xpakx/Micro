package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentDto;
import io.github.xpakx.micro2.comment.dto.CommentRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping()
public class CommentController {
    private final CommentService service;

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDto> addNewComment(@RequestBody CommentRequest request, @PathVariable Long postId, Principal principal) {
        return new ResponseEntity<>(
                service.addComment(request, principal.getName(), postId),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDto> updatePost(@RequestBody CommentRequest request,  @PathVariable Long commentId, Principal principal) {
        return new ResponseEntity<>(
                service.updateComment(request, commentId, principal.getName()),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePost(@PathVariable Long commentId, Principal principal) {
        service.deleteComment(commentId, principal.getName());
        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }
}
