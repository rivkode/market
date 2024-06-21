package sample.market.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserStoreTest {

    @Autowired
    private UserStore userStore;

    @DisplayName("유저의 정보를 올바르게 입력할 경우 저장된다.")
    @Test
    void store() {
        // given
        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();


        // when
        User savedUser = userStore.store(user);

        // then
        assertThat(savedUser.getUsername()).isEqualTo("username");
        assertThat(savedUser.getEmail()).isEqualTo("email@email.com");
    }


}