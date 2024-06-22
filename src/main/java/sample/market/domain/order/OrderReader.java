package sample.market.domain.order;

import java.util.List;

public interface OrderReader {

    Order getOrder(Long orderId);

    List<Order> getCompletedProducts(Long buyerId);

    List<Order> getInitProducts(Long buyerId);
}
