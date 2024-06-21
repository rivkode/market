package sample.market.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderStore orderStore;

    @Override
    public OrderInfo registerOrder(OrderCommand command) {
        Order initOrder = command.toEntity();
        Order order = orderStore.store(initOrder);
        return new OrderInfo(order);
    }
}
