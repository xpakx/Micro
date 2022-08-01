package io.github.xpakx.micro2.user;

import io.github.xpakx.micro2.user.dto.UserAvatarData;
import io.github.xpakx.micro2.user.dto.UserName;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
public class UserPublicController {
    private final UserPublicService service;

    @GetMapping("/users/name")
    public ResponseEntity<List<UserName>> autocomplete(@RequestParam("start") String start)
    {
        return new ResponseEntity<>(
                service.autocomplete(start), HttpStatus.OK
        );
    }

    @GetMapping("/avatar")
    public ResponseEntity<UserAvatarData> getAvatarData(Principal principal)
    {
        return new ResponseEntity<>(
                service.getAvatarData(principal.getName()), HttpStatus.OK
        );
    }
}
