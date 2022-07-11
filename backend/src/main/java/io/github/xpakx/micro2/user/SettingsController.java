package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.user.dto.ChangeGenderRequest;
import io.github.xpakx.micro2.user.dto.ChangePasswordRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@AllArgsConstructor
@RestController
public class SettingsController {
    private final SettingsService service;

    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAccount> changePassword(@RequestBody ChangePasswordRequest password, Principal principal) {
        return new ResponseEntity<>(
                service.changePassword(password, principal.getName()),
                HttpStatus.OK
        );
    }

    @PutMapping("/gender")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAccount> changeGender(@RequestBody ChangeGenderRequest request, Principal principal) {
        return new ResponseEntity<>(
                service.changeGender(request, principal.getName()),
                HttpStatus.OK
        );
    }
}
