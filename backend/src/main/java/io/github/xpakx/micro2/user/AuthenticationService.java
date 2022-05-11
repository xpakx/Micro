package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.user.dto.AuthenticationRequest;
import io.github.xpakx.micro2.user.dto.AuthenticationResponse;
import io.github.xpakx.micro2.user.dto.RegistrationRequest;
import io.github.xpakx.micro2.user.error.JwtBadCredentialsException;
import io.github.xpakx.micro2.user.error.UserDisabledException;
import io.github.xpakx.micro2.security.JwtTokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse generateAuthenticationToken(AuthenticationRequest authenticationRequest) {
        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());
        authenticate(userDetails.getUsername(), authenticationRequest.getPassword());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .build();
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new UserDisabledException("User " +username+" disabled!");
        } catch (BadCredentialsException e) {
            throw new JwtBadCredentialsException("Invalid password!");
        }
    }

    public AuthenticationResponse register(RegistrationRequest request) {
        testRegistrationRequest(request);
        UserAccount userToAdd = createNewUser(request);
        authenticate(request.getUsername(), request.getPassword());
        final String token = jwtTokenUtil.generateToken(userService.userAccountToUserDetails(userToAdd));
        return AuthenticationResponse.builder()
                .token(token)
                .username(userToAdd.getUsername())
                .build();
    }

    private void testRegistrationRequest(RegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ValidationException("Username exists!");
        }
        if (!request.getPassword().equals(request.getPasswordRe())) {
            throw new ValidationException("Passwords don't match!");
        }
    }

    private UserAccount createNewUser(RegistrationRequest request) {
        Set<UserRole> roles = new HashSet<>();
        UserAccount userToAdd = new UserAccount();
        userToAdd.setPassword(passwordEncoder.encode(request.getPassword()));
        userToAdd.setUsername(request.getUsername());
        userToAdd.setRoles(roles);
        userToAdd = userRepository.save(userToAdd);
        userToAdd.setGender("");
        userToAdd.setAvatarUrl("");
        userToAdd.setConfirmed(false);
        return userToAdd;
    }
}
