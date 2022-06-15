package io.github.xpakx.micro2.message;

import io.github.xpakx.micro2.message.dto.MessageDetails;
import io.github.xpakx.micro2.message.dto.MessageMin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateMessageRepository extends PagingAndSortingRepository<PrivateMessage, Long> {
    long countDistinctByRecipientUsernameAndReadIsFalse(String username);
    Page<MessageMin> getAllByRecipientUsername(String username, Pageable pageable);
    List<PrivateMessage> findAllByRecipientUsernameAndReadIsFalse(String username);

    Optional<PrivateMessage> findByRecipientUsernameAndId(String username, Long id);
    Page<MessageDetails> findByRecipientUsernameAndSenderUsernameAndId(String recipientUsername, String senderUsername, Long id, Pageable pageable);
}