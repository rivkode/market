package sample.market.domain.order;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCommand {
    private final Long buyerId;
    private final Long productId;
    private final Integer price;

    public Order toEntity() {
        return Order.builder()
                .buyerId(buyerId)
                .productId(productId)
                .price(price)
                .build();
    }

}
