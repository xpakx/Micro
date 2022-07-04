package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.RoleRequest;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AdministrationController {
    private final AdministrationService service;

    @PostMapping("/administration/users/{username}/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserAccount> addNewRole(@RequestBody RoleRequest request, @PathVariable String username) {
        return new ResponseEntity<>(
                service.addRole(username, request),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/administration/users/{username}/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserAccount> deleteRole(@RequestBody RoleRequest request, @PathVariable String username) {
        return new ResponseEntity<>(
                service.deleteRole(username, request),
                HttpStatus.OK
        );
    }
}
