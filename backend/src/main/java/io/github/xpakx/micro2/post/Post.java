package io.github.xpakx.micro2.post;

import io.github.xpakx.micro2.user.UserAccount;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 20000)
    @NotEmpty
    @Size(max= 20000, message = "Post can't be longer than 20.000 characters!")
    String content;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    UserAccount user;

    LocalDateTime createdAt;
    boolean edited;
}
