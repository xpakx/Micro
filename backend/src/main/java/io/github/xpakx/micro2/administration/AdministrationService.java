package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.RoleRequest;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.UserRole;
import io.github.xpakx.micro2.user.UserRoleRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdministrationService {
    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;

    public UserAccount addRole(String username, RoleRequest role) {
        UserAccount user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        user.getRoles().add(
                roleRepository.findByAuthority(role.getName())
                        .orElse(getNewRole(role.getName()))
        );
        return userRepository.save(user);
    }

    private UserRole getNewRole(String name) {
        UserRole role = new UserRole();
        role.setAuthority(name);
        return role;
    }

    @Transactional
    public UserAccount deleteRole(String username, RoleRequest role) {
        UserAccount user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        user.setRoles(
                user.getRoles().stream()
                        .filter((a) -> !a.getAuthority().equals(role.getName()))
                        .collect(Collectors.toSet())
        );
        return userRepository.save(user);
    }
}
