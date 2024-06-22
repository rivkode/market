package sample.market.domain.order;


import java.util.List;
import sample.market.domain.product.ProductCommand.RetrievePurchaseProduct;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsByBuyer;

public interface OrderService {
    OrderInfo registerOrder(OrderCommand.RegisterOrder command);

    List<Order> retrieveCompletedOrders(RetrievePurchaseProduct command);

    List<Order> retrieveInitOrders(RetrieveReservedProductsByBuyer command);
}
