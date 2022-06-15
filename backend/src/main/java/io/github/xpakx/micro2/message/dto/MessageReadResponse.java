package io.github.xpakx.micro2.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageReadResponse {
    private boolean read;
}
