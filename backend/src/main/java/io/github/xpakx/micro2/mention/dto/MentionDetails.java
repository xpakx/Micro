package io.github.xpakx.micro2.mention.dto;

import io.github.xpakx.micro2.post.dto.PostWithOnlyId;
import io.github.xpakx.micro2.user.dto.UserMin;

public interface MentionDetails {
    Long getId();
    UserMin getAuthor();
    UserMin getMentioned();
    PostWithOnlyId getPost();
}
