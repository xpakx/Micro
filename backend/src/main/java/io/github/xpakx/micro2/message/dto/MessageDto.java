package io.github.xpakx.micro2.message.dto;

import io.github.xpakx.micro2.message.PrivateMessage;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageDto {
    private Long id;
    private String content;
    private String username;

    public static MessageDto fromMessage(PrivateMessage message) {
        MessageDto transformed = new MessageDto();
        transformed.setId(message.getId());
        transformed.setContent(message.getContent());
        transformed.setUsername(message.getRecipient().getUsername());
        return  transformed;
    }
}
