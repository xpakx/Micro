package io.github.xpakx.micro2.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.micro2.follows.UserFollows;
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
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @JsonIgnore
    private String password;

    private String gender;
    private String avatarUrl;
    private boolean confirmed;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_roles",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "user_role_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<UserRole> roles;

    @OneToOne(mappedBy = "user")
    private UserFollows follows;
}
