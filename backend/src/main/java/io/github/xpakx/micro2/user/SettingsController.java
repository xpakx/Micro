package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.user.dto.ChangeGenderRequest;
import io.github.xpakx.micro2.user.dto.ChangePasswordRequest;
import io.github.xpakx.micro2.user.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;

@AllArgsConstructor
@RestController
public class SettingsController {
    private final SettingsService service;

    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> changePassword(@RequestBody ChangePasswordRequest password, Principal principal) {
        return new ResponseEntity<>(
                service.changePassword(password, principal.getName()),
                HttpStatus.OK
        );
    }

    @PutMapping("/gender")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> changeGender(@RequestBody ChangeGenderRequest request, Principal principal) {
        return new ResponseEntity<>(
                service.changeGender(request, principal.getName()),
                HttpStatus.OK
        );
    }

    @PutMapping("/avatar")
    public ResponseEntity<UserDto> uploadAvatar(MultipartFile file, Principal principal) {
        return new ResponseEntity<>(
                service.uploadAvatar(file, principal.getName()),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/avatar/{username}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getAvatar(@PathVariable String username) throws IOException {
        final ByteArrayResource inputStream = service.getAvatar(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .body(inputStream);

    }
}
