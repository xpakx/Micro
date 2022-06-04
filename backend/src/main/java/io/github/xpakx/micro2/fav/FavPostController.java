package io.github.xpakx.micro2.fav;

import io.github.xpakx.micro2.fav.dto.FavDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/posts/{postId}/fav")
public class FavPostController {
    private final FavPostService service;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavDto> addToFav(@PathVariable Long postId, Principal principal) {
        return new ResponseEntity<>(
                service.addToFav(principal.getName(), postId),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteFromFav(@PathVariable Long postId, Principal principal) {
        service.deleteFromFav(principal.getName(), postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
