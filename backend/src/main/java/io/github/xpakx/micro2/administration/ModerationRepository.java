package io.github.xpakx.micro2.administration;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModerationRepository extends PagingAndSortingRepository<Moderation, Long> {
}