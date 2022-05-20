package io.github.xpakx.micro2.tag;

import io.github.xpakx.micro2.tag.dto.TagDetails;
import io.github.xpakx.micro2.tag.dto.TagName;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class TagController {
    private final TagService service;

    @GetMapping("/tags/top")
    public ResponseEntity<List<TagDetails>> getTopTags()
    {
        return new ResponseEntity<>(
                service.getTopTags(), HttpStatus.OK
        );
    }

    @GetMapping("/tags/name")
    public ResponseEntity<List<TagName>> autocomplete(@RequestParam("start") String start)
    {
        return new ResponseEntity<>(
                service.autocomplete(start), HttpStatus.OK
        );
    }
}
