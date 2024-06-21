package sample.market.application.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.market.domain.user.UserCommand;
import sample.market.domain.user.UserInfo;
import sample.market.domain.user.UserService;

// Transactional 범위에 속하지 않음
@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    public UserInfo registerUser(UserCommand command) {
        UserInfo userInfo = userService.registerUser(command);
        // email 발송 등 기타 전송서비스
        return userInfo;
    }

}
