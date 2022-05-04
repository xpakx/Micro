package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDetails;
import io.github.xpakx.micro2.post.dto.PostDto;
import io.github.xpakx.micro2.post.dto.PostRequest;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostDto addPost(PostRequest request, String username) {
        Post newPost = new Post();
        newPost.setContent(request.getMessage());
        newPost.setUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Not such user!"))
        );
        newPost.setEdited(false);
        newPost.setCreatedAt(LocalDateTime.now());
        return PostDto.fromPost(postRepository.save(newPost));
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post toDelete = postRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(PostNotFoundException::new);
        postRepository.delete(toDelete); //TODO: maybe weak deletion? not sure
    }

    public PostDto updatePost(PostRequest request, Long postId, String username) {
        Post toUpdate = postRepository.findByIdAndUserUsername(postId, username)
                .orElseThrow(PostNotFoundException::new);
        toUpdate.setContent(request.getMessage());
        toUpdate.setEdited(true);
        return PostDto.fromPost(postRepository.save(toUpdate));
    }

    public Page<PostDetails> getPosts(Integer page) {
        return postRepository.getAll(
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
    }

    public Page<PostDetails> getPostsByUsername(Integer page, String username) {
        return postRepository.getAllByUserUsername(
                username,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
    }
}
