package io.github.xpakx.micro2.mention;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Mention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean read;
    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne
    private Post post;

    @JsonIgnore
    @ManyToOne
    private UserAccount mentioned;

    @JsonIgnore
    @ManyToOne
    private UserAccount author;
}
