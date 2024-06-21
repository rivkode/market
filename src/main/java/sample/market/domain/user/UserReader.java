package sample.market.domain.user;

public interface UserReader {
    User getUser(Long userId);

    User getUser(String username);

}
