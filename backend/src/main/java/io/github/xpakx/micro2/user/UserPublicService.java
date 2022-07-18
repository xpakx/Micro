package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.user.dto.UserName;
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
}
