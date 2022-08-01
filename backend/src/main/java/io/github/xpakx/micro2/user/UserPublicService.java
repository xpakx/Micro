package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.user.dto.UserAvatarData;
import io.github.xpakx.micro2.user.dto.UserName;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserPublicService {
    private final UserRepository userRepository;

    public List<UserName> autocomplete(String start) {
        return userRepository.findFirst10ByUsernameStartsWith(start);
    }

    public UserAvatarData getAvatarData(String username) {
        return userRepository.getProjectedByUsername(username).orElseThrow(UserNotFoundException::new);
    }
}
