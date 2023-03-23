package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.user.dto.ChangeGenderRequest;
import io.github.xpakx.micro2.user.dto.ChangePasswordRequest;
import io.github.xpakx.micro2.user.dto.UserDto;
import io.github.xpakx.micro2.user.error.FileEmptyException;
import io.github.xpakx.micro2.user.error.FileSaveException;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.validation.ValidationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class SettingsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Path root = Paths.get("avatars");
    Logger logger = LoggerFactory.getLogger(SettingsService.class);

    @PostConstruct
    public void init() {
        try {
            if(!Files.exists(root)) {
                Files.createDirectory(root);
            } else if(!Files.isDirectory(root)) {
                logger.error("Cannot create directory for file upload!");
            }
        } catch (IOException e) {
            logger.error("Cannot create directory for file upload!");
        }
    }

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

    public UserDto uploadAvatar(MultipartFile file, String username) {
        if (file.isEmpty()) {
            throw new FileEmptyException();
        }
        UserAccount toChange = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        final String filename = username + ".png";
        try {
            Files.deleteIfExists(this.root.resolve(filename));
            Files.copy(file.getInputStream(), this.root.resolve(filename));
        } catch (Exception e) {
            throw new FileSaveException();
        }
        toChange.setAvatarUrl("/avatar/"+username);
        return UserDto.of(userRepository.save(toChange));
    }

    public ByteArrayResource getAvatar(String username) throws IOException {
        return new ByteArrayResource(Files.readAllBytes(Paths.get(
                "./avatars/"+username+".png"
        )));
    }
}
