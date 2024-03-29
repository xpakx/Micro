package io.github.xpakx.micro2.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.micro2.comment.Comment;
import io.github.xpakx.micro2.fav.FavPost;
import io.github.xpakx.micro2.mention.Mention;
import io.github.xpakx.micro2.tag.Tag;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    Integer likeCount;
    Integer dislikeCount;

    LocalDateTime createdAt;
    boolean edited;
    boolean deleted;

    @JsonIgnore
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
    private List<Comment> comments;

    @JsonIgnore
    @ManyToMany(cascade={CascadeType.MERGE})
    @JoinTable(name="post_tag",
            joinColumns={@JoinColumn(name="post_id")},
            inverseJoinColumns={@JoinColumn(name="tag_id")})
    private Set<Tag> tags;

    @JsonIgnore
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<FavPost> favorite;

    @JsonIgnore
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
    private List<Mention> mentions;

    private String attachmentUrl;
}
