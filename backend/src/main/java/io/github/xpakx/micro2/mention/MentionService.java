package io.github.xpakx.micro2.mention;

import io.github.xpakx.micro2.mention.dto.MentionCountResponse;
import io.github.xpakx.micro2.mention.dto.MentionDetails;
import io.github.xpakx.micro2.mention.dto.MentionReadRequest;
import io.github.xpakx.micro2.mention.dto.MentionReadResponse;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.user.UserAccount;
import io.github.xpakx.micro2.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        mention.setCreatedAt(LocalDateTime.now());
        return mention;
    }

    public MentionCountResponse getMentionCount(String username) {
        return new MentionCountResponse(
                mentionRepository.countDistinctByMentionedUsernameAndReadIsFalse(username)
        );
    }

    public Page<MentionDetails> getMentions(String username, Integer page) {
        return mentionRepository.getAllByMentionedUsername(
                username,
                PageRequest.of(page, 20, Sort.by("createdAt").descending())
        );
    }

    public MentionReadResponse readMention(MentionReadRequest request, String username, Long mentionId) {
        Mention mention = mentionRepository.findByMentionedUsernameAndId(username, mentionId)
                .orElseThrow();
        mention.setRead(request.isRead());
        mentionRepository.save(mention);
        return new MentionReadResponse(true);
    }

    public MentionReadResponse readMentions(MentionReadRequest request, String username) {
        List<Mention> mentions = mentionRepository.findAllByMentionedUsernameAndReadIsFalse(username);
        mentions.forEach((m) -> m.setRead(request.isRead()));
        mentionRepository.saveAll(mentions);
        return new MentionReadResponse(request.isRead());
    }
}
