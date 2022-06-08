package io.github.xpakx.micro2.follows;

import io.github.xpakx.micro2.user.UserAccount;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowPostRepository extends PagingAndSortingRepository<UserAccount, Long> {
}
