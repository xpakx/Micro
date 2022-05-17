package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.post.dto.PostWithComments;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PostPublicViewController {
    private final PostService service;

    @GetMapping("/posts")
    public ResponseEntity<Page<PostDetails>> getAllPosts()
    {
        return new ResponseEntity<>(
                service.getPosts(0), HttpStatus.OK
        );
    }

    @GetMapping("/posts/{page}")
    public ResponseEntity<Page<PostDetails>> getAllPosts(@PathVariable Integer page)
    {
        return new ResponseEntity<>(
                service.getPosts(page), HttpStatus.OK
        );
    }

    @GetMapping("/user/{username}/posts")
    public ResponseEntity<Page<PostDetails>> getAllPostsByUsername(@PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getPostsByUsername(0, username), HttpStatus.OK
        );
    }

    @GetMapping("/user/{username}/posts/{page}")
    public ResponseEntity<Page<PostDetails>> getAllPostsByUsername(@PathVariable Integer page, @PathVariable String username)
    {
        return new ResponseEntity<>(
                service.getPostsByUsername(page,username), HttpStatus.OK
        );
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostWithComments> getSinglePosts(@PathVariable Long postId)
    {
        return new ResponseEntity<>(
                service.getSinglePost(postId), HttpStatus.OK
        );
    }
}
