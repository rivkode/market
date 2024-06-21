package sample.market.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductStore;
import sample.market.domain.user.User;
import sample.market.domain.user.UserStore;

@SpringBootTest
@Transactional
class OrderStoreTest {
    @Autowired
    private UserStore userStore;

    @Autowired
    private ProductStore productStore;

    @Autowired
    private OrderStore orderStore;

    @DisplayName("상품거래가 시작되면 거래는 주문자와 상품 Id를 가진다")
    @Test
    void store() {
        // given
        User seller = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        User buyer = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        userStore.store(seller);
        userStore.store(buyer);

        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(seller.getId())
                .build();
        productStore.store(product1);

        Order order = Order.builder()
                .buyerId(buyer.getId())
                .productId(product1.getId())
                .price(product1.getPrice())
                .build();

        // when
        Order savedOrder = orderStore.store(order);

        // then
        assertThat(savedOrder.getBuyerId()).isEqualTo(buyer.getId());
        assertThat(savedOrder.getProductId()).isEqualTo(product1.getId());
        assertThat(savedOrder.getPrice()).isEqualTo(product1.getPrice());
    }

}