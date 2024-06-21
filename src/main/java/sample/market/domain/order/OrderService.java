package sample.market.domain.order;


public interface OrderService {
    OrderInfo registerOrder(OrderCommand command);

}
