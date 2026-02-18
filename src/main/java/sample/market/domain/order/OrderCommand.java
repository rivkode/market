package sample.market.domain.order;

import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class OrderCommand {

    @Getter
    @Builder
    public static class RegisterOrder {
        private final Long buyerId;
        private final Long productId;

        public Order toEntity(Integer price) {
            return Order.builder()
                    .buyerId(buyerId)
                    .productId(productId)
                    .price(price)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RetrieveOrders {
        private final Long buyerId;
        private final Long productId;
        private final List<Order.Status> statuses;

        public static RetrieveOrders of(Long buyerId, Long productId) {
            return RetrieveOrders.builder()
                    .buyerId(buyerId)
                    .productId(productId)
                    .statuses(Arrays.asList(Order.Status.values()))
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
    public static class ReserveOrder {
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
