package io.github.xpakx.micro2.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.xpakx.micro2.user.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrivateMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20000)
    @NotEmpty
    @Size(max= 20000, message = "Private message can't be longer than 20.000 characters!")
    String content;

    private boolean read;
    private LocalDateTime createdAt;

    @JsonIgnore
    @ManyToOne
    private UserAccount recipient;

    @JsonIgnore
    @ManyToOne
    private UserAccount sender;
}
