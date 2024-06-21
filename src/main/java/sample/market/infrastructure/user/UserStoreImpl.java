package sample.market.infrastructure.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.market.domain.user.User;
import sample.market.domain.user.UserStore;

@Component
@RequiredArgsConstructor
public class UserStoreImpl implements UserStore {

    private final UserRepository userRepository;
    @Override
    public User store(User user) {
        return userRepository.save(user);
    }
}
