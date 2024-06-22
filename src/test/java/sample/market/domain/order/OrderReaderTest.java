package sample.market.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
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
class OrderReaderTest {

    @Autowired
    private UserStore userStore;

    @Autowired
    private ProductStore productStore;

    @Autowired
    private OrderStore orderStore;

    @Autowired
    private OrderReader orderReader;


    @DisplayName("orderId로 주문 조회")
    @Test
    void getOrderByOrderId() {
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
        orderStore.store(order);

        // when
        Order getOrder = orderReader.getOrder(order.getId());


        // then
        assertThat(getOrder.getPrice()).isEqualTo(product1.getPrice());
        assertThat(getOrder.getProductId()).isEqualTo(product1.getId());
        assertThat(getOrder.getBuyerId()).isEqualTo(buyer.getId());
    }

    @DisplayName("구매자는 완료된 거래를 가져온다.")
    @Test
    void getPurchasedProducts() {
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

        order.complete();
        orderStore.store(order);

        // when
        List<Order> orders = orderReader.getCompletedProducts(buyer.getId());

        // then
        assertThat(orders).hasSize(1)
                .extracting("buyerId", "productId")
                .containsExactlyInAnyOrder(
                        tuple(buyer.getId(), product1.getId())
                );

    }

    @DisplayName("구매자는 시작된 거래를 가져온다.")
    @Test
    void getInitProducts() {
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
        orderStore.store(order);

        // when
        List<Order> orders = orderReader.getInitProducts(buyer.getId());

        // then
        assertThat(orders).hasSize(1)
                .extracting("buyerId", "productId")
                .containsExactlyInAnyOrder(
                        tuple(buyer.getId(), product1.getId())
                );
    }




}