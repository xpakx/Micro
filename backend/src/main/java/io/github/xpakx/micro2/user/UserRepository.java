package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.user.dto.UserName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    List<UserName> findFirst10ByUsernameStartsWith(@Param("username") String name);
}
