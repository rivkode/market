package sample.market.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @DisplayName("입력된 값으로 유저를 등록한다.")
    @Test
    void registerUser() {
        // given
        UserCommand userCommand = UserCommand.builder()
                .email("email@email.com")
                .username("username")
                .password("password")
                .build();


        // when
        UserInfo savedUserInfo = userService.registerUser(userCommand);

        // then
        assertThat(savedUserInfo.getEmail()).isEqualTo("email@email.com");
        assertThat(savedUserInfo.getUsername()).isEqualTo("username");
    }


}