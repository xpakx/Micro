package io.github.xpakx.micro2.fav;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavPostRepository extends JpaRepository<FavPost, Long> {
    boolean existsByPostIdAndUserUsername(Long postId, String username);
    Optional<FavPost> findByPostIdAndUserUsername(Long postId, String username);
}
