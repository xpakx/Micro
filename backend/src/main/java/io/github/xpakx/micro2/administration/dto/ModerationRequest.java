package io.github.xpakx.micro2.administration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModerationRequest {
    private String reason;
    private boolean delete;
}
