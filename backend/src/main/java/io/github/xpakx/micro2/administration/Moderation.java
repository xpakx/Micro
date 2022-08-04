package io.github.xpakx.micro2.administration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Moderation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean moderated;
    private LocalDateTime createdAt;
    private LocalDateTime moderatedAt;
    private String reason;

    @JsonIgnore
    @ManyToOne
    private Post post;

    @JsonIgnore
    @ManyToOne
    private Comment comment;


    @JsonIgnore
    @ManyToOne
    private UserAccount author;

    @JsonIgnore
    @ManyToOne
    private UserAccount moderator;
}
