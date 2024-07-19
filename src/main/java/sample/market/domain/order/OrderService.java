package sample.market.domain.order;


import java.util.List;
import sample.market.domain.order.OrderCommand.ApproveOrder;
import sample.market.domain.order.OrderCommand.CancelOrder;
import sample.market.domain.order.OrderCommand.CompleteOrder;
import sample.market.domain.order.OrderCommand.ReserveOrder;
import sample.market.domain.product.ProductCommand.RetrievePurchaseProduct;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsByBuyer;

public interface OrderService {
    OrderInfo registerOrder(OrderCommand.RegisterOrder command);

    List<Order> retrieveCompletedOrders(RetrievePurchaseProduct command);

    List<Order> retrieveInitOrders(RetrieveReservedProductsByBuyer command);

    OrderInfo approveOrder(ApproveOrder command);

    OrderInfo completeOrder(CompleteOrder command);

    OrderInfo reserveOrder(ReserveOrder command);

    OrderInfo cancelOrder(CancelOrder command);
}
