package io.github.xpakx.micro2.mention;

import io.github.xpakx.micro2.mention.dto.MentionDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentionRepository extends PagingAndSortingRepository<Mention, Long> {
    long countDistinctByMentionedUsernameAndReadIsFalse(String username);
    Page<MentionDetails> getAllByMentionedUsername(String username, Pageable pageable);
    List<Mention> findAllByMentionedUsernameAndReadIsFalse(String username);

    Optional<Mention> findByUserUsernameAndId(String username, Long id);
}