package sample.market.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserReaderTest {

    @Autowired
    private UserStore userStore;

    @Autowired
    private UserReader userReader;

    @DisplayName("유저 ID로 유저 조회")
    @Test
    void getUserWithUserId() {
        // given
        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();


        // when
        userStore.store(user);

        User geUser = userReader.getUser(user.getId());

        // then
        assertThat(geUser.getEmail()).isEqualTo("email@email.com");
        assertThat(geUser.getUsername()).isEqualTo("username");
    }

    @DisplayName("username으로 유저 조회")
    @Test
    void getUserWithUsername() {
        // given
        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("iamuser")
                .build();


        // when
        User savedUser = userStore.store(user);

        User geUser = userReader.getUser(user.getUsername());

        // then
        assertThat(geUser.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(geUser.getUsername()).isEqualTo(savedUser.getUsername());
    }



}