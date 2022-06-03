package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.post.dto.PostWithComments;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class PostPublicViewController {
    private final PostService service;

    @GetMapping("/posts")
    public ResponseEntity<Page<PostWithComments>> getAllPosts(@RequestParam("page") Optional<Integer> page)
    {
        return new ResponseEntity<>(
                service.getPosts(page.orElse(0)), HttpStatus.OK
        );
    }

    @GetMapping("/users/{username}/posts")
    public ResponseEntity<Page<PostWithComments>> getAllPostsByUsername(@PathVariable String username, @RequestParam("page") Optional<Integer> page)
    {
        return new ResponseEntity<>(
                service.getPostsByUsername(page.orElse(0), username), HttpStatus.OK
        );
    }

    @GetMapping("/tags/{name}/posts")
    public ResponseEntity<Page<PostWithComments>> getAllPostsByTag(@PathVariable String name, @RequestParam("page") Optional<Integer> page)
    {
        return new ResponseEntity<>(
                service.getPostsByTagName(page.orElse(0), name), HttpStatus.OK
        );
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostWithComments> getSinglePostWithComments(@PathVariable Long postId)
    {
        return new ResponseEntity<>(
                service.getSinglePostWithComments(postId), HttpStatus.OK
        );
    }

    @GetMapping("/posts/{postId}/min")
    public ResponseEntity<PostDetails> getSinglePost(@PathVariable Long postId)
    {
        return new ResponseEntity<>(
                service.getSinglePost(postId), HttpStatus.OK
        );
    }

    @GetMapping("/posts/hot")
    public ResponseEntity<Page<PostWithComments>> getHotPosts(@RequestParam("page") Optional<Integer> page)
    {
        return new ResponseEntity<>(
                service.getHotPosts(page.orElse(0)), HttpStatus.OK
        );
    }

    @GetMapping("/posts/active")
    public ResponseEntity<Page<PostWithComments>> getActivePosts(@RequestParam("page") Optional<Integer> page)
    {
        return new ResponseEntity<>(
                service.getActivePosts(page.orElse(0)), HttpStatus.OK
        );
    }
}
