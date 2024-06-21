package sample.market.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

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
class ProductReaderTest {
    @Autowired
    private ProductStore productStore;

    @Autowired
    private UserStore userStore;

    @Autowired
    private ProductReader productReader;

    @DisplayName("등록된 제품에는 예약상태를 포함한다.")
    @Test
    void withProductStatus() {
        // given
        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        userStore.store(user);

        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(user.getId())
                .build();
        productStore.store(product1);


        // when
        Product getProduct = productReader.getProduct(product1.getId());


        // then
        assertThat(getProduct.getStatus()).isEqualTo(Status.PREPARE);
    }



}