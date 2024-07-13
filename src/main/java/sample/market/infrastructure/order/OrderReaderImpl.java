package sample.market.infrastructure.order;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.market.domain.order.Order;
import sample.market.domain.order.Order.Status;
import sample.market.domain.order.OrderReader;

@Component
@RequiredArgsConstructor
public class OrderReaderImpl implements OrderReader {

    private final OrderRepository orderRepository;

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

    }

    @Override
    public List<Order> getOrdersComplete(Long buyerId) {
        return orderRepository.findByBuyerIdAndStatus(buyerId, Status.ORDER_COMPLETE);
    }

    @Override
    public List<Order> getOrdersInit(Long buyerId) {
        return orderRepository.findByBuyerIdAndStatus(buyerId, Status.INIT);
    }

    @Override
    public List<Order> getOrdersReserve(Long buyerId) {
        return orderRepository.findByBuyerIdAndStatus(buyerId, Status.ORDER_RESERVE);
    }

    @Override
    public Boolean existsByProductIdAndStatusNotOrderComplete(Long productId) {
        return orderRepository.existsByProductIdAndStatusNotOrderComplete(productId);
    }
}
