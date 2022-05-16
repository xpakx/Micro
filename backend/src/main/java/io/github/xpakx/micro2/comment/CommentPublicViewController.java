package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CommentPublicViewController {
    private final CommentService service;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentDetails>> getAllPosts(@PathVariable Long postId)
    {
        return new ResponseEntity<>(
                service.getCommentsForPost(0, postId), HttpStatus.OK
        );
    }

    @GetMapping("/posts/{postId}/comments/{page}")
    public ResponseEntity<Page<CommentDetails>> getAllPosts(@PathVariable Integer page, @PathVariable Long postId)
    {
        return new ResponseEntity<>(
                service.getCommentsForPost(page, postId), HttpStatus.OK
        );
    }
}
