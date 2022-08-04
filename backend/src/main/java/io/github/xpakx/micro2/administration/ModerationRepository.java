package io.github.xpakx.micro2.administration;

import io.github.xpakx.micro2.administration.dto.ModerationDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModerationRepository extends PagingAndSortingRepository<Moderation, Long> {
    Page<ModerationDetails> findAllByModeratedIsFalse(Pageable pageable);
    Page<ModerationDetails> findAllBy(Pageable pageable);

    Page<ModerationDetails> findByModeratedTrueAndDeletedTrueAndAuthorUsername(String username, Pageable pageable);

    Page<ModerationDetails> findByReportedByUsername(String username, Pageable pageable);

}