package io.github.xpakx.micro2.follows;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowsRepository extends JpaRepository<UserFollows, Long> {
    @Query("select u from UserFollows u where u.user.username = ?1")
    @EntityGraph("followed-users")
    Optional<UserFollows> findWithUsers(String username);

    @Query("select u from UserFollows u where u.user.username = ?1")
    @EntityGraph("followed-tags")
    Optional<UserFollows> findWIthTags(String username);
}
