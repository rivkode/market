package sample.market.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import sample.market.common.base.BaseEntity;
import sample.market.common.util.TokenGenerator;

@Entity
@Getter
public class User extends BaseEntity {

    private static final String USER_PREFIX = "usr_";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String userToken;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Getter
    public enum Role {
        ROLE_ADMIN("admin"), ROLE_USER("user");
        private String description;
        Role(String description) {
            this.description = description;
        }
    }

    @Builder
    public User(String username, String email, String password, String role) {
        if (username == null || username.length() == 0) throw new IllegalArgumentException("empty username");
        if (email == null || email.length() == 0) throw new IllegalArgumentException("empty email");
        if (password == null || password.length() == 0) throw new IllegalArgumentException("empty passowrd");

        this.userToken = TokenGenerator.randomCharacterWithPrefix(USER_PREFIX);
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = Role.ROLE_USER;
    }

    public User() {

    }
}
