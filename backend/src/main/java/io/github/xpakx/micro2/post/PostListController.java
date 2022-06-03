package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.post.dto.PostWithComments;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/user/{username}/post")
public class PostListController {
    private final PostService service;

    @GetMapping("/posts")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Page<PostWithComments>> getAllPosts(@PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getPosts(0), HttpStatus.OK
        );
    }

    @GetMapping("/posts/{page}")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Page<PostWithComments>> getAllPosts(@PathVariable Integer page, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getPosts(page), HttpStatus.OK
        );
    }

    @GetMapping("/user/{user}/posts")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Page<PostWithComments>> getAllPostsByUsername(@PathVariable String user, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getPostsByUsername(0, user), HttpStatus.OK
        );
    }

    @GetMapping("/user/{user}/posts/{page}")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Page<PostWithComments>> getAllPostsByUsername(@PathVariable Integer page, @PathVariable String user, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getPostsByUsername(page,user), HttpStatus.OK
        );
    }

    @GetMapping("/tags/{name}/posts")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Page<PostWithComments>> getAllPostsByTag(@PathVariable String name, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getPostsByTagName(0, name), HttpStatus.OK
        );
    }

    @GetMapping("/tags/{name}/posts/{page}")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Page<PostWithComments>> getAllPostsByTag(@PathVariable Integer page, @PathVariable String name, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getPostsByTagName(page,name), HttpStatus.OK
        );
    }

    @GetMapping("/post/{postId}")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<PostWithComments> getSinglePostWithComments(@PathVariable Long postId, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getSinglePostWithComments(postId), HttpStatus.OK
        );
    }

    @GetMapping("/post/{postId}/min")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<PostDetails> getSinglePost(@PathVariable Long postId, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getSinglePost(postId), HttpStatus.OK
        );
    }

    @GetMapping("/posts/hot")
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<Page<PostWithComments>> getHotPosts(@PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getHotPosts(0), HttpStatus.OK
        );
    }

    @GetMapping("/posts/hot/{page}")
    public ResponseEntity<Page<PostWithComments>> getHotPosts(@PathVariable Integer page, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getHotPosts(page), HttpStatus.OK
        );
    }

    @GetMapping("/posts/active")
    public ResponseEntity<Page<PostWithComments>> getActivePosts(@PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getActivePosts(0), HttpStatus.OK
        );
    }

    @GetMapping("/posts/active/{page}")
    public ResponseEntity<Page<PostWithComments>> getActivePosts(@PathVariable Integer page, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getActivePosts(page), HttpStatus.OK
        );
    }
}
