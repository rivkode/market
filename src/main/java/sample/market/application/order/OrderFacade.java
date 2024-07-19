package sample.market.application.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.market.domain.order.OrderCommand.*;
import sample.market.domain.order.OrderInfo;
import sample.market.domain.order.OrderService;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;

    public OrderInfo registerOrder(RegisterOrder command) {
        OrderInfo orderInfo = orderService.registerOrder(command);
        return orderInfo;
    }

    public OrderInfo approveOrder(ApproveOrder command) {
        OrderInfo orderInfo = orderService.approveOrder(command);
        return orderInfo;
    }

    public OrderInfo completeOrder(CompleteOrder command) {
        OrderInfo orderInfo = orderService.completeOrder(command);
        return orderInfo;
    }

    public OrderInfo reserveOrder(ReserveOrder command) {
        OrderInfo orderInfo = orderService.reserveOrder(command);
        return orderInfo;
    }

    public OrderInfo cancelOrder(CancelOrder command) {
        OrderInfo orderInfo = orderService.cancelOrder(command);
        return orderInfo;
    }
}
