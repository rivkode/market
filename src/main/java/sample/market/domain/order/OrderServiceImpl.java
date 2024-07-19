package sample.market.domain.order;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sample.market.application.stock.StockFacade;
import sample.market.domain.order.Order.Status;
import sample.market.domain.order.OrderCommand.ApproveOrder;
import sample.market.domain.order.OrderCommand.CancelOrder;
import sample.market.domain.order.OrderCommand.CompleteOrder;
import sample.market.domain.order.OrderCommand.ReserveOrder;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductCommand.RetrievePurchaseProduct;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsByBuyer;
import sample.market.domain.product.ProductManager;
import sample.market.domain.product.ProductReader;

@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderStore orderStore;
    private final OrderReader orderReader;
    private final StockFacade stockFacade;
    private final ProductReader productReader;
    private final ProductManager productManager;

    @Override
    @Transactional
    public OrderInfo registerOrder(OrderCommand.RegisterOrder command) {
        Integer price = productReader.getProduct(command.getProductId()).getPrice();
        Order initOrder = command.toEntity(price);
        Order order = orderStore.store(initOrder);
        stockFacade.decreaseWithRedissonLock(command.getProductId());

        return new OrderInfo(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> retrieveCompletedOrders(RetrievePurchaseProduct command) {
        List<Order> orders = orderReader.getOrdersComplete(command.getBuyerId());
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> retrieveInitOrders(RetrieveReservedProductsByBuyer command) {
        List<Order> orders = orderReader.getOrdersInit(command.getBuyerId());
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

        productManager.updateProductStatus(order.getProductId());


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

        productManager.updateProductStatus(order.getProductId());

        return new OrderInfo(order);
    }

    @Override
    @Transactional
    public OrderInfo reserveOrder(ReserveOrder command) {
        Order order = orderReader.getOrder(command.getOrderId());

        if (!order.getStatus().equals(Status.ORDER_SALE_APPROVED)) {
            throw new IllegalStateException(
                    "거래 Id : " + order.getId() + " 거래 구매 확정시 " + order.getStatus() + " 이며 APPROVED 되지 않았습니다.");
        }
        order.reserve();

        productManager.updateProductStatus(order.getProductId());

        return new OrderInfo(order);
    }

    @Override
    @Transactional
    public OrderInfo cancelOrder(CancelOrder command) {
        Order order = orderReader.getOrder(command.getOrderId());

        // 구매가 완료되었을 경우 취소 불가 (환불요청)

        if (order.getStatus().equals(Status.ORDER_COMPLETE)) {
            throw new IllegalStateException(
                    "거래 Id : " + order.getId() + " / 해당 거래 상태는 " + order.getStatus() + " 이며 이미 완료된 거래는 취소가 불가합니다.");
        }
        order.cancel();
        // 수량 감소

        return new OrderInfo(order);
    }
}
