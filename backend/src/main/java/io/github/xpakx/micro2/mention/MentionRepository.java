package io.github.xpakx.micro2.mention;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentionRepository extends JpaRepository<Mention, Long> {
    long countDistinctByMentionedUsernameAndReadIsFalse(String username);

}