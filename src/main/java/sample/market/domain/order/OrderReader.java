package sample.market.domain.order;

import java.util.List;

public interface OrderReader {

    Order getOrder(Long orderId);

    List<Order> getPurchasedProducts(Long buyerId);
}
