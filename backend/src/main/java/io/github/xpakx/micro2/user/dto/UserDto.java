package io.github.xpakx.micro2.user.dto;

import io.github.xpakx.micro2.user.UserAccount;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    String username;
    String gender;
    String avatarUrl;
    boolean confirmed;

    public static UserDto of(UserAccount user) {
        UserDto transformed = new UserDto();
        transformed.setUsername(user.getUsername());
        transformed.setGender(user.getGender());
        transformed.setAvatarUrl(user.getAvatarUrl());
        transformed.setConfirmed(user.isConfirmed());
        return transformed;
    }
}
