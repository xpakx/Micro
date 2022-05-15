package io.github.xpakx.micro2.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.micro2.post.Post;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 20000)
    @NotEmpty
    @Size(max= 20000, message = "Comment can't be longer than 20.000 characters!")
    String content;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    UserAccount user;

    LocalDateTime createdAt;
    boolean edited;
    Integer likeCount;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
