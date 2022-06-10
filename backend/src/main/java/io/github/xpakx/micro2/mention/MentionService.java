package io.github.xpakx.micro2.mention;

import io.github.xpakx.micro2.mention.dto.MentionCountResponse;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.post.PostRepository;
import io.github.xpakx.micro2.tag.Tag;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MentionService {
    private final MentionRepository mentionRepository;
    private final UserRepository userRepository;
    private static final int MAX_MENTIONS = 15;

    public List<Mention> addMentions(String message, UserAccount user, Post post) {
        List<String> allMentions = new ArrayList<String>();
        Matcher m = Pattern.compile("(\\s|\\A|>)@(\\w+)")
                .matcher(message);

        while (m.find()) {
            allMentions.add(m.group(2));
        }

        return allMentions.stream()
                .distinct()
                .limit(MAX_MENTIONS)
                .map((me) -> createNewMention(me, user, post))
                .filter((me) -> me.getMentioned() != null)
                .distinct()
                .collect(Collectors.toList());
    }

    private Mention createNewMention(String name, UserAccount user, Post post) {
        Mention mention = new Mention();
        mention.setAuthor(user);
        mention.setPost(post);
        mention.setMentioned(userRepository.findByUsername(name).orElse(null));
        mention.setId(null);
        mention.setRead(false);
        return mention;
    }

    public MentionCountResponse getMentionCount(String username) {
        return new MentionCountResponse(
                mentionRepository.countDistinctByMentionedUsernameAndReadIsFalse(username)
        );
    }
}
