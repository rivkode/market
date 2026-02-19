package sample.market.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.market.domain.order.Order.Status;
import sample.market.infrastructure.order.OrderReaderImpl;
import sample.market.infrastructure.order.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderReaderTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderReaderImpl orderReader;

    @DisplayName("orderId로 주문 조회")
    @Test
    void getOrderByOrderId() {
        Order order = Order.builder()
                .buyerId(2L)
                .productId(1L)
                .price(1000)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order getOrder = orderReader.getOrder(1L);

        assertThat(getOrder.getPrice()).isEqualTo(1000);
        assertThat(getOrder.getProductId()).isEqualTo(1L);
        assertThat(getOrder.getBuyerId()).isEqualTo(2L);
    }

    @DisplayName("구매자는 완료된 거래를 가져온다.")
    @Test
    void getPurchasedProducts() {
        Order order = Order.builder()
                .buyerId(2L)
                .productId(1L)
                .price(1000)
                .build();
        order.complete();
        when(orderRepository.findByBuyerIdAndStatus(2L, Status.ORDER_COMPLETE))
                .thenReturn(List.of(order));

        List<Order> orders = orderReader.getOrdersComplete(2L);

        assertThat(orders).hasSize(1)
                .extracting("buyerId", "productId")
                .containsExactlyInAnyOrder(
                        tuple(2L, 1L)
                );
    }

    @DisplayName("구매자는 시작된 거래를 가져온다.")
    @Test
    void getInitProducts() {
        Order order = Order.builder()
                .buyerId(2L)
                .productId(1L)
                .price(1000)
                .build();
        when(orderRepository.findByBuyerIdAndStatus(2L, Status.INIT))
                .thenReturn(List.of(order));

        List<Order> orders = orderReader.getOrdersInit(2L);

        assertThat(orders).hasSize(1)
                .extracting("buyerId", "productId")
                .containsExactlyInAnyOrder(
                        tuple(2L, 1L)
                );
    }
}
