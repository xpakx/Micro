package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.security.JwtTokenUtils;
import io.github.xpakx.micro2.user.dto.AuthenticationRequest;
import io.github.xpakx.micro2.user.dto.AuthenticationResponse;
import io.github.xpakx.micro2.user.error.JwtBadCredentialsException;
import io.github.xpakx.micro2.user.error.UserDisabledException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenUtils jwtTokenUtil;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthenticationService service;

    void injectMocks() {
        service = new AuthenticationService(authenticationManager, jwtTokenUtil, userService, userRepository, passwordEncoder);
    }

    @Test
    void shouldNotAuthenticateWithBadCredentials() {
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(BadCredentialsException.class);
        given(userService.loadUserByUsername("user"))
                .willReturn(getUserWithPassword("password1"));
        injectMocks();

        assertThrows(
                JwtBadCredentialsException.class,
                () -> service.generateAuthenticationToken(getAuthRequest("user", "password"))
        );
    }

    private UserDetails getUserWithPassword(String password) {
        return new User("user", password, new ArrayList<>());
    }

    private AuthenticationRequest getAuthRequest(String user, String password) {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setPassword(password);
        request.setUsername(user);
        return request;
    }

    @Test
    void shouldNotAuthenticateDisabledUser() {
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(UserDisabledException.class);
        given(userService.loadUserByUsername("user"))
                .willReturn(getUserWithPassword("password"));
        injectMocks();

        assertThrows(
                UserDisabledException.class,
                () -> service.generateAuthenticationToken(getAuthRequest("user", "password"))
        );
    }

    @Test
    void shouldAuthenticate() {
        given(userService.loadUserByUsername("user"))
                .willReturn(getUserWithPassword("password"));
        given(jwtTokenUtil.generateToken(any(UserDetails.class)))
                .willReturn("test-token");
        injectMocks();

        AuthenticationResponse response = service.generateAuthenticationToken(getAuthRequest("user", "password"));

        assertNotNull(response);
        assertThat(response.getToken(), is("test-token"));
        assertThat(response.getUsername(), is("user"));
    }
}
