package sample.market.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.market.application.stock.StockFacade;
import sample.market.domain.order.Order.Status;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductCommand;
import sample.market.domain.product.ProductManager;
import sample.market.domain.product.ProductReader;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderStore orderStore;
    @Mock
    private OrderReader orderReader;
    @Mock
    private StockFacade stockFacade;
    @Mock
    private ProductReader productReader;
    @Mock
    private ProductManager productManager;

    @InjectMocks
    private OrderServiceImpl orderService;

    @DisplayName("상품거래가 시작되면 거래는 주문자와 상품 Id를 가진다")
    @Test
    void registerOrder() {
        Product product = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(1L)
                .build();

        OrderCommand.RegisterOrder command = OrderCommand.RegisterOrder.builder()
                .buyerId(2L)
                .productId(1L)
                .build();

        when(productReader.getProduct(1L)).thenReturn(product);
        when(orderStore.store(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderInfo orderInfo = orderService.registerOrder(command);

        assertThat(orderInfo.getBuyerId()).isEqualTo(2L);
        assertThat(orderInfo.getProductId()).isEqualTo(1L);
        assertThat(orderInfo.getPrice()).isEqualTo(1000);
        verify(stockFacade).decreaseWithRedissonLock(1L);
    }

    @DisplayName("구매자는 자신의 완료된 거래를 가져온다.")
    @Test
    void retrieveCompletedOrders() {
        ProductCommand.RetrievePurchaseProduct command = ProductCommand.RetrievePurchaseProduct.builder()
                .buyerId(2L)
                .build();

        Order order1 = Order.builder().buyerId(2L).productId(1L).price(1000).build();
        Order order2 = Order.builder().buyerId(2L).productId(2L).price(3000).build();
        order1.complete();
        order2.complete();
        when(orderReader.getOrdersComplete(2L)).thenReturn(List.of(order1, order2));

        List<Order> orders = orderService.retrieveCompletedOrders(command);

        assertThat(orders.get(0).getBuyerId()).isEqualTo(2L);
        assertThat(orders.get(0).getProductId()).isEqualTo(order1.getProductId());
        assertThat(orders.get(1).getBuyerId()).isEqualTo(2L);
        assertThat(orders.get(1).getProductId()).isEqualTo(order2.getProductId());
    }

    @DisplayName("구매자는 시작된 거래를 가져온다.")
    @Test
    void retrieveInitOrders() {
        ProductCommand.RetrieveReservedProductsByBuyer command = ProductCommand.RetrieveReservedProductsByBuyer.builder()
                .buyerId(2L)
                .build();

        Order order1 = Order.builder().buyerId(2L).productId(1L).price(1000).build();
        Order order2 = Order.builder().buyerId(2L).productId(2L).price(3000).build();
        when(orderReader.getOrdersInit(2L)).thenReturn(List.of(order1, order2));

        List<Order> orders = orderService.retrieveInitOrders(command);

        assertThat(orders).hasSize(2)
                .extracting("buyerId", "productId", "status", "price")
                .containsExactlyInAnyOrder(
                        tuple(2L, 1L, order1.getStatus(), order1.getPrice()),
                        tuple(2L, 2L, order2.getStatus(), order2.getPrice())
                );
    }

    @DisplayName("판매자가 구매상품에 대해 판매승인을 한다.")
    @Test
    void approveOrder() {
        Product product = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(1L)
                .build();
        Order order = Order.builder()
                .productId(1L)
                .buyerId(2L)
                .price(1000)
                .build();

        OrderCommand.ApproveOrder command = OrderCommand.ApproveOrder.builder()
                .productId(1L)
                .orderId(1L)
                .sellerId(1L)
                .build();

        when(productReader.getProduct(1L)).thenReturn(product);
        when(orderReader.getOrder(1L)).thenReturn(order);

        OrderInfo approvedOrderInfo = orderService.approveOrder(command);

        assertThat(approvedOrderInfo.getStatus()).isEqualTo(Status.ORDER_SALE_APPROVED);
        verify(productManager).updateProductStatus(1L);
    }

    @DisplayName("판매승인시 승인 요청 판매자의 Id와 상품의 판매자 Id가 다를시 예외가 발생한다.")
    @Test
    void approveOrderWithDifferentSellerId() {
        Product product = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(1L)
                .build();

        long differentSellerId = 101L;
        OrderCommand.ApproveOrder command = OrderCommand.ApproveOrder.builder()
                .productId(1L)
                .orderId(1L)
                .sellerId(differentSellerId)
                .build();

        when(productReader.getProduct(1L)).thenReturn(product);

        assertThatThrownBy(() -> orderService.approveOrder(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 Id : " + product.getId() + "상품 판매 승인시 요청 판매자의 Id " + command.getSellerId() + "와 상품의 판매자 Id :"+ product.getSellerId() +"가 다릅니다.");
    }

    @DisplayName("구매자가 구매상품에 대해 구매 확정을 한다.")
    @Test
    void completeOrder() {
        Order order = Order.builder()
                .productId(1L)
                .buyerId(2L)
                .price(1000)
                .build();
        order.approve();

        OrderCommand.CompleteOrder command = OrderCommand.CompleteOrder.builder()
                .productId(1L)
                .orderId(1L)
                .sellerId(1L)
                .build();
        when(orderReader.getOrder(1L)).thenReturn(order);

        OrderInfo completedOrderInfo = orderService.completeOrder(command);

        assertThat(completedOrderInfo.getStatus()).isEqualTo(Status.ORDER_COMPLETE);
        verify(productManager).updateProductStatus(1L);
    }

    @DisplayName("구매 확정시 거래의 상태가 Approved가 아닐경우 예외가 발생 한다.")
    @Test
    void completeOrderWithNotApprovedStatus() {
        Order order = Order.builder()
                .productId(1L)
                .buyerId(2L)
                .price(1000)
                .build();

        OrderCommand.CompleteOrder command = OrderCommand.CompleteOrder.builder()
                .productId(1L)
                .orderId(1L)
                .sellerId(1L)
                .build();
        when(orderReader.getOrder(1L)).thenReturn(order);

        assertThatThrownBy(() -> orderService.completeOrder(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("거래 Id : " + order.getId() + " 거래 구매 확정시 " + order.getStatus() + " 이며 APPROVED 되지 않았습니다.");
    }

    @DisplayName("남은 수량이 0이며 OrderComplete가 아닌 Order가 1개 이상일경우 Product 상태는 예약중이다.")
    @Test
    void registerOrderByProductReserve() {
        Product product = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(1L)
                .build();
        when(productReader.getProduct(1L)).thenReturn(product);
        when(orderStore.store(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order1 = Order.builder().buyerId(2L).productId(1L).price(1000).build();
        Order order2 = Order.builder().buyerId(2L).productId(1L).price(1000).build();
        when(orderReader.getOrder(null)).thenReturn(order1, order2, order2);

        OrderInfo orderInfo = orderService.registerOrder(OrderCommand.RegisterOrder.builder().buyerId(2L).productId(1L).build());
        orderService.approveOrder(OrderCommand.ApproveOrder.builder()
                .productId(1L).orderId(orderInfo.getOrderId()).sellerId(1L).build());

        OrderInfo orderInfo1 = orderService.registerOrder(OrderCommand.RegisterOrder.builder().buyerId(2L).productId(1L).build());
        orderService.approveOrder(OrderCommand.ApproveOrder.builder()
                .productId(1L).orderId(orderInfo1.getOrderId()).sellerId(1L).build());
        orderService.completeOrder(OrderCommand.CompleteOrder.builder()
                .productId(1L).orderId(orderInfo1.getOrderId()).sellerId(1L).build());

        assertThat(order1.getStatus()).isEqualTo(Status.ORDER_SALE_APPROVED);
        assertThat(order2.getStatus()).isEqualTo(Status.ORDER_COMPLETE);
        verify(productManager, times(3)).updateProductStatus(1L);
    }

    @DisplayName("남은 수량이 0이며 모든 Order가 Complete일 경우 Product 상태는 완료이다.")
    @Test
    void registerOrderByProductComplete() {
        Product product = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(1L)
                .build();
        when(productReader.getProduct(1L)).thenReturn(product);
        when(orderStore.store(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order1 = Order.builder().buyerId(2L).productId(1L).price(1000).build();
        Order order2 = Order.builder().buyerId(2L).productId(1L).price(1000).build();
        when(orderReader.getOrder(null)).thenReturn(order1, order1, order2, order2);

        OrderInfo orderInfo = orderService.registerOrder(OrderCommand.RegisterOrder.builder().buyerId(2L).productId(1L).build());
        orderService.approveOrder(OrderCommand.ApproveOrder.builder()
                .productId(1L).orderId(orderInfo.getOrderId()).sellerId(1L).build());
        orderService.completeOrder(OrderCommand.CompleteOrder.builder()
                .productId(1L).orderId(orderInfo.getOrderId()).sellerId(1L).build());

        OrderInfo orderInfo1 = orderService.registerOrder(OrderCommand.RegisterOrder.builder().buyerId(2L).productId(1L).build());
        orderService.approveOrder(OrderCommand.ApproveOrder.builder()
                .productId(1L).orderId(orderInfo1.getOrderId()).sellerId(1L).build());
        orderService.completeOrder(OrderCommand.CompleteOrder.builder()
                .productId(1L).orderId(orderInfo1.getOrderId()).sellerId(1L).build());

        assertThat(order1.getStatus()).isEqualTo(Status.ORDER_COMPLETE);
        assertThat(order2.getStatus()).isEqualTo(Status.ORDER_COMPLETE);
        verify(productManager, times(4)).updateProductStatus(1L);
    }
}
