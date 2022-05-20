package io.github.xpakx.micro2.tag;

import io.github.xpakx.micro2.tag.dto.TagDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private static final int MAX_TAGS = 15;

    public Set<Tag> addTags(String message) {
        List<String> allMentions = new ArrayList<String>();
        Matcher m = Pattern.compile("(\\s|\\A|>)#(\\w+)")
                .matcher(message);

        while (m.find()) {
            allMentions.add(m.group(2));
        }

        Set<Tag> tags = allMentions.stream()
                .map(String::toLowerCase)
                .distinct()
                .limit(MAX_TAGS)
                .map(this::processTag)
                .collect(Collectors.toSet());
        tagRepository.saveAll(tags.stream().filter(tag -> tag.getId() == null).collect(Collectors.toList()));
        return tags;
    }

    private Tag processTag(String name) {
        return tagRepository.findByName(name).orElse(createNewTag(name));
    }

    private Tag createNewTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setId(null);
        return tag;
    }

    public List<TagDetails> getTopTags() {
        return tagRepository.getTopTagsAfterDate(LocalDateTime.now().minusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0));
    }
}
