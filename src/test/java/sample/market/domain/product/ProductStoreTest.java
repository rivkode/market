package sample.market.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.market.domain.product.Product.Status;
import sample.market.domain.user.User;
import sample.market.domain.user.UserStore;

@SpringBootTest
@Transactional
class ProductStoreTest {

    @Autowired
    private ProductStore productStore;

    @Autowired
    private UserStore userStore;

    @DisplayName("등록된 제품에는 제품명, 가격, 예약상태가 포함되어야 한다.")
    @Test
    void store() {
        // given
        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        userStore.store(user);

        Product product = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(user.getId())
                .build();


        // when
        Product savedProduct = productStore.store(product);

        // then
        assertThat(savedProduct.getName()).isEqualTo("마스크");
        assertThat(savedProduct.getPrice()).isEqualTo(1000);
        assertThat(savedProduct.getStatus()).isEqualTo(Status.PREPARE);
    }

    @DisplayName("sellerId가 없을 경우 예외가 발생한다.")
    @Test
    void withoutSellerId() {
        // given
        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        // user가 저장되지 않아 userId가 존재하지 않음

        // when // then
        assertThatThrownBy(() -> Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(user.getId())
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("invalid sellerId");
    }
}