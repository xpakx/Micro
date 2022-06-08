package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("posts")
public class PostController {
    private final PostService service;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDto> addNewPost(@RequestBody PostRequest request, Principal principal) {
        return new ResponseEntity<>(
                service.addPost(request, principal.getName()),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Principal principal) {
        service.deletePost(postId, principal.getName());
        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }

    @PutMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDto> updatePost(@RequestBody PostRequest request, @PathVariable Long postId, Principal principal) {
        return new ResponseEntity<>(
                service.updatePost(request, postId, principal.getName()),
                HttpStatus.OK
        );
    }

    @GetMapping("/fav")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PostWithComments>> getFavPosts(@RequestParam("page") Optional<Integer> page, Principal principal)
    {
        return new ResponseEntity<>(
                service.getFavoritePosts(page.orElse(0), principal.getName()), HttpStatus.OK
        );
    }

    @GetMapping("/follows/tags")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PostWithComments>> getFollowedFromUsers(@RequestParam("page") Optional<Integer> page, Principal principal)
    {
        return new ResponseEntity<>(
                service.getPostsFromFollowedTags(page.orElse(0), principal.getName()), HttpStatus.OK
        );
    }

    @GetMapping("/follows/users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PostWithComments>> getFollowedFromTags(@RequestParam("page") Optional<Integer> page, Principal principal)
    {
        return new ResponseEntity<>(
                service.getPostsByFollowedUsers(page.orElse(0), principal.getName()), HttpStatus.OK
        );
    }
}
