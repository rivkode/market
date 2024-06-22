package sample.market.domain.order;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.market.domain.product.ProductCommand.RetrievePurchaseProduct;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsByBuyer;

@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderStore orderStore;
    private final OrderReader orderReader;

    @Override
    public OrderInfo registerOrder(OrderCommand.RegisterOrder command) {
        Order initOrder = command.toEntity();
        Order order = orderStore.store(initOrder);
        return new OrderInfo(order);
    }

    @Override
    public List<Order> retrieveCompletedOrders(RetrievePurchaseProduct command) {
        List<Order> orders = orderReader.getCompletedProducts(command.getBuyerId());
        return orders;
    }

    @Override
    public List<Order> retrieveInitOrders(RetrieveReservedProductsByBuyer command) {
        List<Order> orders = orderReader.getInitProducts(command.getBuyerId());
        return orders;
    }
}
