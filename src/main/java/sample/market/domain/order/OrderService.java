package sample.market.domain.order;


import java.util.List;
import sample.market.domain.product.ProductCommand.RetrievePurchaseProduct;

public interface OrderService {
    OrderInfo registerOrder(OrderCommand.RegisterOrder command);

    List<Order> retrieveCompletedOrders(RetrievePurchaseProduct command);
}
