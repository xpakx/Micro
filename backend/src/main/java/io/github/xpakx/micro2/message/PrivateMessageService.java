package io.github.xpakx.micro2.message;

import io.github.xpakx.micro2.message.dto.*;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import io.github.xpakx.micro2.user.error.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PrivateMessageService {
    private final PrivateMessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageDto sendMessage(MessageRequest request, String senderUsername, String recipientUsername) {
        PrivateMessage newMessage = new PrivateMessage();
        newMessage.setContent(request.getContent());
        UserAccount sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new UserNotFoundException("Not such user!"));
        UserAccount recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new UserNotFoundException("Not such user!"));
        newMessage.setSender(sender);
        newMessage.setRecipient(recipient);
        newMessage.setRead(false);
        newMessage.setCreatedAt(LocalDateTime.now());
        return MessageDto.fromMessage(messageRepository.save(newMessage));
    }

    public MessageCountResponse getMessageCount(String username) {
        return new MessageCountResponse(
                messageRepository.countDistinctByRecipientUsernameAndReadIsFalse(username)
        );
    }

    public Page<MessageMin> getMessageList(String username, Integer page) {
        return messageRepository.getAllByRecipientUsername(
                username,
                PageRequest.of(page, 20, Sort.by("read").and(Sort.by("createdAt").descending()))
        );
    }

    public MessageDetails getSingleMessage(Long messageId, String username) {
        MessageDetails response = messageRepository.findProjectedByRecipientUsernameAndId(username, messageId)
                .orElseThrow();
        messageRepository.updateReadById(messageId);
        return response;
    }
}
