package io.github.xpakx.micro2.follows;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.micro2.tag.Tag;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraphs({
        @NamedEntityGraph(name = "followed-users",
                attributeNodes = {@NamedAttributeNode("users")}
        ),
        @NamedEntityGraph(name = "followed-tags",
                attributeNodes = {@NamedAttributeNode("tags")}
        )
})
public class UserFollows {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id", nullable = false)
    UserAccount user;

    @JsonIgnore
    @ManyToMany(cascade={CascadeType.MERGE})
    @JoinTable(name="follow_tag",
            joinColumns={@JoinColumn(name="follow_id")},
            inverseJoinColumns={@JoinColumn(name="tag_id")})
    private Set<Tag> tags;

    @JsonIgnore
    @ManyToMany(cascade={CascadeType.MERGE})
    @JoinTable(name="follow_user",
            joinColumns={@JoinColumn(name="follow_id")},
            inverseJoinColumns={@JoinColumn(name="user_id")})
    private Set<UserAccount> users;
}
