package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.user.dto.ChangeGenderRequest;
import io.github.xpakx.micro2.user.dto.ChangePasswordRequest;
import io.github.xpakx.micro2.user.dto.UserDto;
import io.github.xpakx.micro2.user.error.FileEmptyException;
import io.github.xpakx.micro2.user.error.FileSaveException;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ValidationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public UserDto uploadAvatar(MultipartFile file, String username) {
        if (file.isEmpty()) {
            throw new FileEmptyException();
        }
        UserAccount toChange = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        final String filename = username + ".png";
        try {
            InputStream in = file.getInputStream();
            File dir = new File("./avatars");
            String path = dir.getAbsolutePath();
            FileOutputStream f = new FileOutputStream(
                    path.substring(0, path.length()-1) + filename);
            int ch = 0;
            while ((ch = in.read()) != -1) {
                f.write(ch);
            }
            f.flush();
            f.close();
        } catch (IOException ex) {
            throw new FileSaveException();
        }
        toChange.setAvatarUrl("/avatars/"+filename);
        return UserDto.of(userRepository.save(toChange));
    }
}
