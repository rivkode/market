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
import sample.market.domain.product.ProductCommand;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsByBuyer;
import sample.market.domain.product.ProductStore;
import sample.market.domain.user.User;
import sample.market.domain.user.UserStore;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private UserStore userStore;

    @Autowired
    private ProductStore productStore;

    @Autowired
    private OrderStore orderStore;

    @Autowired
    private OrderService orderService;

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

        OrderCommand.RegisterOrder command = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product1.getId())
                .price(product1.getPrice())
                .build();

        // when
        OrderInfo orderInfo = orderService.registerOrder(command);

        // then
        assertThat(orderInfo.getBuyerId()).isEqualTo(buyer.getId());
        assertThat(orderInfo.getProductId()).isEqualTo(product1.getId());
        assertThat(orderInfo.getPrice()).isEqualTo(product1.getPrice());
    }

    @DisplayName("구매자는 자신의 완료된 거래를 가져온다.")
    @Test
    void retrieveCompletedOrders() {
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

        Product product2 = Product.builder()
                .price(3000)
                .name("충전기")
                .sellerId(seller.getId())
                .build();

        productStore.storeAll(List.of(product1, product2));

        ProductCommand.RetrievePurchaseProduct command = ProductCommand.RetrievePurchaseProduct.builder()
                .buyerId(buyer.getId())
                .build();

        OrderCommand.RegisterOrder orderCommand1 = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product1.getId())
                .price(product1.getPrice())
                .build();

        OrderCommand.RegisterOrder orderCommand2 = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product2.getId())
                .price(product2.getPrice())
                .build();

        Order order1 = orderCommand1.toEntity();
        Order order2 = orderCommand2.toEntity();

        order1.complete();
        order2.complete();

        orderStore.store(order1);
        orderStore.store(order2);

        // when
        List<Order> orders = orderService.retrieveCompletedOrders(command);

        // then
        assertThat(orders.get(0).getBuyerId()).isEqualTo(buyer.getId());
        assertThat(orders.get(0).getProductId()).isEqualTo(order1.getProductId());
        assertThat(orders.get(1).getBuyerId()).isEqualTo(buyer.getId());
        assertThat(orders.get(1).getProductId()).isEqualTo(order2.getProductId());
    }

    @DisplayName("구매자는 시작된 거래를 가져온다.")
    @Test
    void retrieveInitOrders() {
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

        Product product2 = Product.builder()
                .price(3000)
                .name("충전기")
                .sellerId(seller.getId())
                .build();

        productStore.storeAll(List.of(product1, product2));

        ProductCommand.RetrieveReservedProductsByBuyer command = ProductCommand.RetrieveReservedProductsByBuyer.builder()
                .buyerId(buyer.getId())
                .build();

        OrderCommand.RegisterOrder orderCommand1 = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product1.getId())
                .price(product1.getPrice())
                .build();

        OrderCommand.RegisterOrder orderCommand2 = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product2.getId())
                .price(product2.getPrice())
                .build();

        Order order1 = orderCommand1.toEntity();
        Order order2 = orderCommand2.toEntity();

        orderStore.store(order1);
        orderStore.store(order2);

        // when
        List<Order> orders = orderService.retrieveInitOrders(command);

        // then
        assertThat(orders).hasSize(2)
                .extracting("buyerId", "productId", "status", "price")
                .containsExactlyInAnyOrder(
                        tuple(buyer.getId(), product1.getId(), order1.getStatus(), order1.getPrice()),
                        tuple(buyer.getId(), product2.getId(), order2.getStatus(), order2.getPrice())
                );

    }

}