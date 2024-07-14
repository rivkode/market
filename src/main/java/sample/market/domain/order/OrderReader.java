package sample.market.domain.order;

import java.util.List;

public interface OrderReader {

    Order getOrder(Long orderId);

    List<Order> getOrdersComplete(Long buyerId);

    List<Order> getOrdersInit(Long buyerId);

    List<Order> getOrdersReserve(Long buyerId);

    Boolean existsByProductIdAndStatusNotOrderComplete(Long productId);
}
