package io.github.xpakx.micro2.user;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository user;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = user.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user with username " + username));
        return new User(userAccount.getUsername(), userAccount.getPassword(), userAccount.getRoles());
    }

    public UserDetails userAccountToUserDetails(UserAccount userAccount) {
        return new User(userAccount.getUsername(), userAccount.getPassword(), userAccount.getRoles());
    }
}
