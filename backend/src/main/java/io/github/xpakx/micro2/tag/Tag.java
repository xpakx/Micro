package io.github.xpakx.micro2.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.micro2.post.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotEmpty
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts;
}
