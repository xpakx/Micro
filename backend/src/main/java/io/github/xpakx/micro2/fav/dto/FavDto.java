package io.github.xpakx.micro2.fav.dto;

import io.github.xpakx.micro2.fav.FavPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FavDto {
    Long postId;
    String userId;
}
