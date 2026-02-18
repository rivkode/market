package sample.market.infrastructure.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.market.domain.order.Order;
import sample.market.domain.order.Order.Status;
import sample.market.domain.order.OrderStore;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductStore;
import sample.market.domain.user.User;
import sample.market.domain.user.UserStore;

@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    private UserStore userStore;

    @Autowired
    private ProductStore productStore;

    @Autowired
    private OrderStore orderStore;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("buyerId와 Status 조건으로 Order를 조회한다.")
    @Test
    void findByBuyerIdAndStatus() {
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
        List<Order> orders = orderRepository.findByBuyerIdAndStatus(buyer.getId(),
                Status.ORDER_COMPLETE);

        // then
        assertThat(orders).hasSize(1)
                .extracting("buyerId", "productId")
                .containsExactlyInAnyOrder(
                        tuple(buyer.getId(), product1.getId())
                );
    }

    @DisplayName("buyerId + productId + status in 조건으로 Order를 조회한다.")
    @Test
    void findByBuyerIdAndProductIdAndStatusIn() {
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

        Product product = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(seller.getId())
                .build();
        productStore.store(product);

        Order order = Order.builder()
                .buyerId(buyer.getId())
                .productId(product.getId())
                .price(product.getPrice())
                .build();
        orderStore.store(order);

        // when
        List<Order> orders = orderRepository.findByBuyerIdAndProductIdAndStatusIn(
                buyer.getId(),
                product.getId(),
                Arrays.asList(Status.values()));

        // then
        assertThat(orders).hasSize(1)
                .extracting("buyerId", "productId")
                .containsExactly(tuple(buyer.getId(), product.getId()));
    }

    @DisplayName("buyerId와 Status 조건으로 Order를 조회한다.")
    @Test
    void existsByProductIdAndStatusNotOrderComplete() {
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

        // order가 complete이 아닌 approve 인 상태
        order.approve();
        orderStore.store(order);

        // when
        boolean existOrderComplete = orderRepository.existsByProductIdAndStatusNotOrderComplete(
                product1.getId());

        // then
        assertThat(existOrderComplete).isTrue();
    }
}
