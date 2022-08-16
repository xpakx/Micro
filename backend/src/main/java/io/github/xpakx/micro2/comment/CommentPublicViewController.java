package io.github.xpakx.micro2.comment;

import io.github.xpakx.micro2.comment.dto.CommentDetails;
import io.github.xpakx.micro2.comment.dto.CommentSearchDetails;
import io.github.xpakx.micro2.comment.dto.CommentWithUserData;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class CommentPublicViewController {
    private final CommentService service;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentWithUserData>> getAllComments(@PathVariable Long postId, @RequestParam("page") Optional<Integer> page, Principal principal)
    {
        return new ResponseEntity<>(
                principal == null ? service.getCommentsForPost(page.orElse(0), postId) : service.getCommentsForPostAuth(page.orElse(0), postId, principal.getName()),
                HttpStatus.OK
        );
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentDetails> getSingleComment(@PathVariable Long commentId)
    {
        return new ResponseEntity<>(
                service.getSingleComment(commentId), HttpStatus.OK
        );
    }

    @GetMapping("/comments/search")
    public ResponseEntity<Page<CommentSearchDetails>> search(@RequestParam("page") Optional<Integer> page, @RequestParam("search") String searchTerm) {
        return new ResponseEntity<>(
                service.searchComments(searchTerm, page.orElse(0)),
                HttpStatus.OK
        );
    }
}
