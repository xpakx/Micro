package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.RoleRequest;
import io.github.xpakx.micro2.comment.dto.CommentDto;
import io.github.xpakx.micro2.comment.dto.CommentRequest;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class AdministrationController {
    private final AdministrationService service;

    @PostMapping("/administration/users/{username}/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserAccount> addNewRole(@RequestBody RoleRequest request, @PathVariable String username) {
        return new ResponseEntity<>(
                service.addRole(username, request),
                HttpStatus.CREATED
        );
    }
}
