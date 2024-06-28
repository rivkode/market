package sample.market.interfaces.order;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.market.domain.order.OrderCommand;
import sample.market.domain.order.OrderInfo;

public class OrderDto {
    @Getter
    @NoArgsConstructor
    public static class RegisterRequest {
        @NotNull(message = "buyerId는 필수입력값입니다.")
        private Long buyerId;

        @NotNull(message = "productId는 필수입력값입니다.")
        private Long productId;

        @NotNull(message = "price는 필수입력값입니다.")
        private Integer price;

        public OrderCommand.RegisterOrder toCommand() {
            return OrderCommand.RegisterOrder.builder()
                    .buyerId(buyerId)
                    .productId(productId)
                    .price(price)
                    .build();
        }

        @Builder
        public RegisterRequest(Long buyerId, Long productId, Integer price) {
            this.buyerId = buyerId;
            this.productId = productId;
            this.price = price;
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
