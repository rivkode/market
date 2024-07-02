package sample.market.domain.order;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sample.market.application.product.stock.StockFacade;
import sample.market.domain.order.Order.Status;
import sample.market.domain.order.OrderCommand.ApproveOrder;
import sample.market.domain.order.OrderCommand.CompleteOrder;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductCommand.RetrievePurchaseProduct;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsByBuyer;
import sample.market.domain.product.ProductReader;
import sample.market.domain.product.stock.Stock;
import sample.market.domain.product.stock.StockReader;

@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderStore orderStore;
    private final OrderReader orderReader;
    private final StockFacade stockFacade;
    private final StockReader stockReader;
    private final ProductReader productReader;

    @Override
    @Transactional
    public OrderInfo registerOrder(OrderCommand.RegisterOrder command) {
        Order initOrder = command.toEntity();
        Order order = orderStore.store(initOrder);
        stockFacade.decreaseWithRedissonLock(command.getProductId());
        // 수량 체크 후 상품에 대한 상태 변경
        Product product = productReader.getProduct(order.getProductId());
        Stock stock = stockReader.getStockByProductId(order.getProductId());
        Long productQuantity = stock.getQuantity();

        // Order의 상태 중 하나라도 거래 완료가 아닌지를 체크하기 위해 해당 Product의 Order들의 상태 리스트를 가져와야 한다.
        boolean existOrderComplete = orderReader.existsByProductIdAndStatusNotOrderComplete(product.getId());

        //만약 남은 수량이 0개이며 구매확정이 모두 되었다면 Product 의 상태는 완료 아니라면 예약중
        if ((productQuantity == 0L)) {
            if (existOrderComplete) {
                product.completed();
            } else {
                product.reserved();
            }
        }

        return new OrderInfo(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> retrieveCompletedOrders(RetrievePurchaseProduct command) {
        List<Order> orders = orderReader.getCompletedProducts(command.getBuyerId());
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> retrieveInitOrders(RetrieveReservedProductsByBuyer command) {
        List<Order> orders = orderReader.getInitProducts(command.getBuyerId());
        return orders;
    }

    @Override
    @Transactional
    public OrderInfo approveOrder(ApproveOrder command) {
        Product product = productReader.getProduct(command.getProductId());
        Long sellerId = product.getSellerId();

        if (!command.getSellerId().equals(sellerId)) {
            throw new IllegalArgumentException(
                    "상품 Id : " + product.getId() + "상품 판매 승인시 요청 판매자의 Id " + command.getSellerId() + "와 상품의 판매자 Id :"+ sellerId +"가 다릅니다.");
        }

        Order order = orderReader.getOrder(command.getOrderId());
        order.approve();

        return new OrderInfo(order);
    }

    @Override
    @Transactional
    public OrderInfo completeOrder(CompleteOrder command) {
        Order order = orderReader.getOrder(command.getOrderId());

        if (!order.getStatus().equals(Status.ORDER_SALE_APPROVED)) {
            throw new IllegalStateException(
                    "거래 Id : " + order.getId() + " 거래 구매 확정시 " + order.getStatus() + " 이며 APPROVED 되지 않았습니다.");
        }
        order.complete();

        return new OrderInfo(order);
    }
}
