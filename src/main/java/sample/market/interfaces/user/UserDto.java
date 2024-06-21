package sample.market.interfaces.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.market.domain.user.UserCommand;
import sample.market.domain.user.UserInfo;

public class UserDto {

    @Getter
    @NoArgsConstructor
    public static class RegisterRequest {
        @NotEmpty(message = "email은 필수입력값입니다.")
        private String email;

        @NotEmpty(message = "username은 필수입력값입니다.")
        private String username;

        @NotEmpty(message = "password는 필수입력값입니다.")
        private String password;

        public UserCommand toCommand() {
            return UserCommand.builder()
                    .email(email)
                    .username(username)
                    .password(password)
                    .build();
        }

        @Builder
        public RegisterRequest(String email, String username, String password) {
            this.email = email;
            this.username = username;
            this.password = password;
        }
    }

    @Getter
    public static class LoginRequest {
        @NotEmpty(message = "email 필수입력값입니다.")
        private String email;

        @NotEmpty(message = "password 필수입력값입니다.")
        private String password;
    }

    @Getter
    public static class RegisterResponse {
        private String username;

        public RegisterResponse(UserInfo userInfo) {
            this.username = userInfo.getUsername();
        }
    }
}
