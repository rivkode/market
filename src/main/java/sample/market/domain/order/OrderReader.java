package sample.market.domain.order;

import java.util.List;
import sample.market.domain.order.Order.Status;

public interface OrderReader {

    Order getOrder(Long orderId);

    List<Order> getOrders(Long buyerId, Long productId, List<Status> statuses);

    List<Order> getOrdersComplete(Long buyerId);

    List<Order> getOrdersInit(Long buyerId);

    List<Order> getOrdersReserve(Long buyerId);

    Boolean existsByProductIdAndStatusNotOrderComplete(Long productId);
}
