package io.github.xpakx.micro2.fav;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavPostRepository extends JpaRepository<FavPost, Long> {
}
