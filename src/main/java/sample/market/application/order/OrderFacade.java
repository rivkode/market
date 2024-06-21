package sample.market.application.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.market.domain.order.OrderCommand;
import sample.market.domain.order.OrderInfo;
import sample.market.domain.order.OrderService;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;

    public OrderInfo registerOrder(OrderCommand.RegisterOrder command) {
        OrderInfo orderInfo = orderService.registerOrder(command);
        return orderInfo;
    }

}
