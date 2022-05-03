package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.post.dto.PostDto;
import io.github.xpakx.micro2.post.dto.PostRequest;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return PostDto.fromPost(postRepository.save(newPost));
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post toDelete = postRepository.findByIdAndUserUsername(id, username)
                .orElseThrow(PostNotFoundException::new);
        postRepository.delete(toDelete); //TODO: maybe weak deletion? not sure
    }
}
