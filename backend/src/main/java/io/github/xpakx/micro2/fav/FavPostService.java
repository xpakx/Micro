package io.github.xpakx.micro2.fav;

import io.github.xpakx.micro2.fav.dto.FavDto;
import io.github.xpakx.micro2.fav.error.PostAlreadyFavoriteException;
import io.github.xpakx.micro2.fav.error.PostNotFavoriteException;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.post.error.PostNotFoundException;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FavPostService {
    private final FavPostRepository favRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public FavDto addToFav(String username, Long postId) {
        if(favRepository.existsByPostIdIdAndUserUsername(postId, username)) {
            throw new PostAlreadyFavoriteException();
        }
        FavPost toAdd = new FavPost();
        toAdd.setPost(postRepository.findById(postId).orElseThrow(PostNotFoundException::new));
        toAdd.setUser(userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new));
        favRepository.save(toAdd);
        return new FavDto(postId, username);
    }

    public void deleteFromFav(String username, Long postId) {
        FavPost toDelete = favRepository.findByPostIdIdAndUserUsername(postId, username)
                .orElseThrow(PostNotFavoriteException::new);
        favRepository.delete(toDelete);
    }
}
