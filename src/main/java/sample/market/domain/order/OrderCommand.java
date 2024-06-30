package sample.market.domain.order;

import lombok.Builder;
import lombok.Getter;

public class OrderCommand {

    @Getter
    @Builder
    public static class RegisterOrder {
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

    @Getter
    @Builder
    public static class ApproveOrder {
        private final Long sellerId;
        private final Long productId;
        private final Long orderId;
    }

    @Getter
    @Builder
    public static class CompleteOrder {
        private final Long sellerId;
        private final Long productId;
        private final Long orderId;
    }
}
