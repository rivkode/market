package sample.market.interfaces.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.market.domain.order.OrderCommand;
import sample.market.domain.order.OrderInfo;

public class OrderDto {
    @Getter
    @NoArgsConstructor
    public static class RegisterRequest {
        @NotNull
        private Long buyerId;

        @NotNull
        private Long productId;

        @NotNull
        private Integer price;

        public OrderCommand.RegisterOrder toCommand() {
            return OrderCommand.RegisterOrder.builder()
                    .buyerId(buyerId)
                    .productId(productId)
                    .price(price)
                    .build();
        }
    }

    @Getter
    public static class RegisterResponse {
        private Long orderId;

        public RegisterResponse(OrderInfo orderInfo) {
            this.orderId = orderInfo.getOrderId();
        }
    }

}
