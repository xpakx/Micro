package io.github.xpakx.micro2.mention;

import io.github.xpakx.micro2.mention.dto.MentionCountResponse;
import io.github.xpakx.micro2.mention.dto.MentionDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class MentionController {
    private final MentionService service;

    @GetMapping("/mentions/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MentionCountResponse> getMentionCount(Principal principal) {
        return new ResponseEntity<MentionCountResponse>(
                service.getMentionCount(principal.getName()),
                HttpStatus.OK
        );
    }

    @GetMapping("/mentions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<MentionDetails>> getAllMentions(@RequestParam("page") Optional<Integer> page, Principal principal) {
        return new ResponseEntity<Page<MentionDetails>>(
                service.getMentions(principal.getName(), page.orElse(0)),
                HttpStatus.OK
        );
    }
}
