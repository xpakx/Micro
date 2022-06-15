package io.github.xpakx.micro2.message;

import io.github.xpakx.micro2.message.dto.MessageDetails;
import io.github.xpakx.micro2.message.dto.MessageMin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateMessageRepository extends PagingAndSortingRepository<PrivateMessage, Long> {
    long countDistinctByRecipientUsernameAndReadIsFalse(String username);
    Page<MessageMin> getAllByRecipientUsername(String username, Pageable pageable);
    List<PrivateMessage> findAllByRecipientUsernameAndReadIsFalse(String username);
    Optional<MessageDetails> findProjectedByRecipientUsernameAndId(String username, Long id);

    @Transactional
    @Modifying
    @Query("update PrivateMessage p set p.read = true where p.id = ?1")
    int updateReadById(Long id);


}