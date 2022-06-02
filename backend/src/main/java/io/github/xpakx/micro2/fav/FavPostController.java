package io.github.xpakx.micro2.fav;

import io.github.xpakx.micro2.fav.dto.FavDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/user/{username}/posts/{postId}/fav")
public class FavPostController {
    private final FavPostService service;

    @PostMapping
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<FavDto> addToFav(@PathVariable Long postId, @PathVariable String username) {
        return new ResponseEntity<>(
                service.addToFav(username, postId),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping
    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<?> deleteFromFav(@PathVariable Long postId, @PathVariable String username) {
        service.deleteFromFav(username, postId);
        return new ResponseEntity<>(                HttpStatus.OK);
    }
}
