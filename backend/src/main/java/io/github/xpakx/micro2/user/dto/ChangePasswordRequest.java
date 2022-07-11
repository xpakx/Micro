package io.github.xpakx.micro2.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotNull
    private String password;
    @NotNull
    private String passwordRe;
}
