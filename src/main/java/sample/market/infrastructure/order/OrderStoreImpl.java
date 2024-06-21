package sample.market.infrastructure.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.market.domain.order.Order;
import sample.market.domain.order.OrderStore;

@Component
@RequiredArgsConstructor
public class OrderStoreImpl implements OrderStore {

    private final OrderRepository orderRepository;


    @Override
    public Order store(Order order) {
        return orderRepository.save(order);
    }
}
