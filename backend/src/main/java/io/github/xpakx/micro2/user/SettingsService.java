package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.user.dto.ChangeGenderRequest;
import io.github.xpakx.micro2.user.dto.ChangePasswordRequest;
import io.github.xpakx.micro2.user.dto.UserDto;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;

@Service
@AllArgsConstructor
public class SettingsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto changePassword(ChangePasswordRequest request, String username) {
        UserAccount toChange = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (!request.getPassword().equals(request.getPasswordRe())) {
            throw new ValidationException("Passwords don't match!");
        }
        toChange.setPassword(passwordEncoder.encode(request.getPassword()));
        return UserDto.of(userRepository.save(toChange));
    }

    public UserDto changeGender(ChangeGenderRequest request, String username) {
        UserAccount toChange = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        toChange.setGender(request.getGender());
        return UserDto.of(userRepository.save(toChange));
    }
}