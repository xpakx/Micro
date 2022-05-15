package io.github.xpakx.micro2.like;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    UserAccount user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private boolean positive;
}
