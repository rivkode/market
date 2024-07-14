package sample.market.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.market.domain.order.Order.Status;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductCommand;
import sample.market.domain.product.ProductStore;
import sample.market.domain.stock.Stock;
import sample.market.domain.stock.StockReader;
import sample.market.domain.stock.StockStore;
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

    @Autowired
    private StockStore stockStore;

    @Autowired
    private StockReader stockReader;

    @DisplayName("상품거래가 시작되면 거래는 주문자와 상품 Id를 가진다")
    @Test
    void registerOrder() {
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

        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(10L)
                .build();
        stockStore.store(stock);

        OrderCommand.RegisterOrder command = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product1.getId())
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
                .build();

        OrderCommand.RegisterOrder orderCommand2 = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product2.getId())
                .build();

        Order order1 = orderCommand1.toEntity(product1.getPrice());
        Order order2 = orderCommand2.toEntity(product2.getPrice());

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
                .build();

        OrderCommand.RegisterOrder orderCommand2 = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product2.getId())
                .build();

        Order order1 = orderCommand1.toEntity(product1.getPrice());
        Order order2 = orderCommand2.toEntity(product2.getPrice());

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

    @DisplayName("판매자가 구매상품에 대해 판매승인을 한다.")
    @Test
    void approveOrder() {
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

        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(100L)
                .build();
        stockStore.store(stock);

        Order order = Order.builder()
                .productId(product1.getId())
                .buyerId(buyer.getId())
                .price(product1.getPrice())
                .build();
        orderStore.store(order);

        OrderCommand.ApproveOrder command = OrderCommand.ApproveOrder.builder()
                .productId(product1.getId())
                .orderId(order.getId())
                .sellerId(seller.getId())
                .build();

        // when
        OrderInfo approvedOrderInfo = orderService.approveOrder(command);

        // then
        assertThat(approvedOrderInfo.getStatus()).isEqualTo(Status.ORDER_SALE_APPROVED);
    }

    @DisplayName("판매승인시 승인 요청 판매자의 Id와 상품의 판매자 Id가 다를시 예외가 발생한다.")
    @Test
    void approveOrderWithDifferentSellerId() {
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

        long differentSellerId = seller.getId() + 100L;

        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(seller.getId())
                .build();
        productStore.store(product1);

        Order order = Order.builder()
                .productId(product1.getId())
                .buyerId(buyer.getId())
                .price(product1.getPrice())
                .build();
        orderStore.store(order);

        OrderCommand.ApproveOrder command = OrderCommand.ApproveOrder.builder()
                .productId(product1.getId())
                .orderId(order.getId())
                .sellerId(differentSellerId)
                .build();


        // when // then
        assertThatThrownBy(() -> orderService.approveOrder(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 Id : " + product1.getId() + "상품 판매 승인시 요청 판매자의 Id " + command.getSellerId() + "와 상품의 판매자 Id :"+ product1.getSellerId() +"가 다릅니다.");
    }

    @DisplayName("구매자가 구매상품에 대해 구매 확정을 한다.")
    @Test
    void completeOrder() {
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

        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(100L)
                .build();
        stockStore.store(stock);

        Order order = Order.builder()
                .productId(product1.getId())
                .buyerId(buyer.getId())
                .price(product1.getPrice())
                .build();
        // order Approved 로 상태 변경
        order.approve();
        orderStore.store(order);

        OrderCommand.CompleteOrder command = OrderCommand.CompleteOrder.builder()
                .productId(product1.getId())
                .orderId(order.getId())
                .sellerId(seller.getId())
                .build();

        // when
        OrderInfo completedOrderInfo = orderService.completeOrder(command);

        // then
        assertThat(completedOrderInfo.getStatus()).isEqualTo(Status.ORDER_COMPLETE);
    }

    @DisplayName("구매 확정시 거래의 상태가 Approved가 아닐경우 예외가 발생 한다.")
    @Test
    void completeOrderWithNotApprovedStatus() {
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
                .productId(product1.getId())
                .buyerId(buyer.getId())
                .price(product1.getPrice())
                .build();
        // order 상태는 INIT 상태
        orderStore.store(order);

        OrderCommand.CompleteOrder command = OrderCommand.CompleteOrder.builder()
                .productId(product1.getId())
                .orderId(order.getId())
                .sellerId(seller.getId())
                .build();

        // when // then
        assertThatThrownBy(() -> orderService.completeOrder(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("거래 Id : " + order.getId() + " 거래 구매 확정시 " + order.getStatus() + " 이며 APPROVED 되지 않았습니다.");
    }

    @DisplayName("남은 수량이 0이며 OrderComplete가 아닌 Order가 1개 이상일경우 Product 상태는 예약중이다.")
    @Test
    void registerOrderByProductReserve() {
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

        // quantity는 2이며 Order의 하나는 complete, 하나는 approve 인 상황을 가정한다
        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(2L)
                .build();
        stockStore.store(stock);

        OrderCommand.RegisterOrder command1 = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product1.getId())
                .build();



        OrderCommand.RegisterOrder command2 = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product1.getId())
                .build();

        // approve 상태가 하나 존재하므로 product 상태는 예약중이다.
        // when
        // 거래 등록
        OrderInfo orderInfo = orderService.registerOrder(command1);

        OrderCommand.ApproveOrder approveCommand = OrderCommand.ApproveOrder.builder()
                .productId(product1.getId())
                .orderId(orderInfo.getOrderId())
                .sellerId(seller.getId())
                .build();

        // 거래 승인
        orderService.approveOrder(approveCommand);

        // 거래 등록
        OrderInfo orderInfo1 = orderService.registerOrder(command2);

        OrderCommand.ApproveOrder approveCommand1 = OrderCommand.ApproveOrder.builder()
                .productId(product1.getId())
                .orderId(orderInfo1.getOrderId())
                .sellerId(seller.getId())
                .build();

        // 거래 승인
        orderService.approveOrder(approveCommand1);

        OrderCommand.CompleteOrder completeCommand = OrderCommand.CompleteOrder.builder()
                .productId(product1.getId())
                .orderId(orderInfo1.getOrderId())
                .sellerId(seller.getId())
                .build();

        // 거래 완료
        orderService.completeOrder(completeCommand);

        Stock getStock = stockReader.getStockByProductId(product1.getId());


        // then
        assertThat(getStock.getQuantity()).isZero();
        assertThat(product1.getStatus()).isEqualTo(Product.Status.RESERVED);
    }

    @DisplayName("남은 수량이 0이며 모든 Order가 Complete일 경우 Product 상태는 완료이다.")
    @Test
    void registerOrderByProductComplete() {
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

        // quantity는 2이며 Order의 하나는 complete, 하나는 approve 인 상황을 가정한다
        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(2L)
                .build();
        stockStore.store(stock);

        OrderCommand.RegisterOrder command1 = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product1.getId())
                .build();



        OrderCommand.RegisterOrder command2 = OrderCommand.RegisterOrder.builder()
                .buyerId(buyer.getId())
                .productId(product1.getId())
                .build();

        // approve 상태가 하나 존재하므로 product 상태는 예약중이다.
        // when
        // 거래 등록
        OrderInfo orderInfo = orderService.registerOrder(command1);

        OrderCommand.ApproveOrder approveCommand = OrderCommand.ApproveOrder.builder()
                .productId(product1.getId())
                .orderId(orderInfo.getOrderId())
                .sellerId(seller.getId())
                .build();

        // 거래 승인
        orderService.approveOrder(approveCommand);

        OrderCommand.CompleteOrder completeCommand = OrderCommand.CompleteOrder.builder()
                .productId(product1.getId())
                .orderId(orderInfo.getOrderId())
                .sellerId(seller.getId())
                .build();

        // 거래 완료
        orderService.completeOrder(completeCommand);

        // 거래 등록
        OrderInfo orderInfo1 = orderService.registerOrder(command2);

        OrderCommand.ApproveOrder approveCommand1 = OrderCommand.ApproveOrder.builder()
                .productId(product1.getId())
                .orderId(orderInfo1.getOrderId())
                .sellerId(seller.getId())
                .build();

        // 거래 승인
        orderService.approveOrder(approveCommand1);

        OrderCommand.CompleteOrder completeCommand1 = OrderCommand.CompleteOrder.builder()
                .productId(product1.getId())
                .orderId(orderInfo1.getOrderId())
                .sellerId(seller.getId())
                .build();

        // 거래 완료
        orderService.completeOrder(completeCommand1);
        Stock getStock = stockReader.getStockByProductId(product1.getId());


        // then
        assertThat(getStock.getQuantity()).isZero();
        assertThat(product1.getStatus()).isEqualTo(Product.Status.END_OF_SALE);
    }
}