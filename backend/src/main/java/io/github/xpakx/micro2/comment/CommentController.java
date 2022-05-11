package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentDto;
import io.github.xpakx.micro2.comment.dto.CommentRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user/{username}")
public class CommentController {
    private final CommentService service;

    @PostMapping("/post/{postId}/comments")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<CommentDto> addNewComment(@RequestBody CommentRequest request, @PathVariable String username, @PathVariable Long postId) {
        return new ResponseEntity<>(
                service.addComment(request, username, postId),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<CommentDto> updatePost(@RequestBody CommentRequest request, @PathVariable String username, @PathVariable Long commentId) {
        return new ResponseEntity<>(
                service.updateComment(request, commentId, username),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<?> deletePost(@PathVariable String username, @PathVariable Long commentId) {
        service.deleteComment(commentId, username);
        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }
}
