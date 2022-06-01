package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.post.dto.PostDto;
import io.github.xpakx.micro2.post.dto.PostRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user/{username}/post")
public class PostController {
    private final PostService service;

    @PostMapping
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<PostDto> addNewPost(@RequestBody PostRequest request, @PathVariable String username) {
        return new ResponseEntity<>(
                service.addPost(request, username),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<?> deletePost(@PathVariable String username, @PathVariable Long postId) {
        service.deletePost(postId, username);
        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }

    @PutMapping("/{postId}")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<PostDto> updatePost(@RequestBody PostRequest request, @PathVariable String username, @PathVariable Long postId) {
        return new ResponseEntity<>(
                service.updatePost(request, postId, username),
                HttpStatus.OK
        );
    }

    @GetMapping("/fav")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Page<PostDetails>> getFavPosts(@PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getFavoritePosts(0, username), HttpStatus.OK
        );
    }

    @GetMapping("/fav/{page}")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Page<PostDetails>> getFavPosts(@PathVariable Integer page, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getFavoritePosts(page,username), HttpStatus.OK
        );
    }
}
