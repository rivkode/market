package sample.market.domain.order;

import lombok.Getter;
import sample.market.domain.order.Order.Status;

@Getter
public class OrderInfo {
    private final Long orderId;
    private final Long buyerId;
    private final Long productId;
    private final Integer price;
    private final Status status;

    public OrderInfo(Order order) {
        this.orderId = order.getId();
        this.buyerId = order.getBuyerId();
        this.productId = order.getProductId();
        this.price = order.getPrice();
        this.status = order.getStatus();
    }


}
