package sample.market.interfaces.order;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.market.domain.order.Order.Status;
import sample.market.domain.order.OrderCommand;
import sample.market.domain.order.OrderInfo;
import sample.market.domain.product.ProductInfo;
import sample.market.interfaces.product.ProductDto;

public class OrderDto {
    @Getter
    @NoArgsConstructor
    public static class RegisterRequest {
        @NotNull(message = "buyerId는 필수입력값입니다.")
        private Long buyerId;

        @NotNull(message = "productId는 필수입력값입니다.")
        private Long productId;

        public OrderCommand.RegisterOrder toCommand() {
            return OrderCommand.RegisterOrder.builder()
                    .buyerId(buyerId)
                    .productId(productId)
                    .build();
        }

        @Builder
        public RegisterRequest(Long buyerId, Long productId) {
            this.buyerId = buyerId;
            this.productId = productId;
        }
    }

    @Getter
    public static class RegisterResponse {
        private Long orderId;

        public RegisterResponse(OrderInfo orderInfo) {
            this.orderId = orderInfo.getOrderId();
        }
    }

    @Getter
    public static class ApproveResponse {
        private Long orderId;
        private Long productId;
        private Status status;

        public ApproveResponse(OrderInfo orderInfo) {
            this.orderId = orderInfo.getOrderId();
            this.productId = orderInfo.getProductId();
            this.status = orderInfo.getStatus();

        }
    }

    @Getter
    @NoArgsConstructor
    public static class ApproveRequest {
        @NotNull(message = "sellerId는 필수입력값입니다.")
        private Long sellerId;

        @NotNull(message = "productId는 필수입력값입니다.")
        private Long productId;

        @NotNull(message = "orderId는 필수입력값입니다.")
        private Long orderId;

        public OrderCommand.ApproveOrder toCommand() {
            return OrderCommand.ApproveOrder.builder()
                    .sellerId(sellerId)
                    .productId(productId)
                    .orderId(orderId)
                    .build();
        }

        @Builder
        public ApproveRequest(Long sellerId, Long productId, Long orderId) {
            this.sellerId = sellerId;
            this.productId = productId;
            this.orderId = orderId;
        }
    }

    @Getter
    public static class CompleteResponse {
        private Long orderId;
        private Long productId;
        private Status status;

        public CompleteResponse(OrderInfo orderInfo) {
            this.orderId = orderInfo.getOrderId();
            this.productId = orderInfo.getProductId();
            this.status = orderInfo.getStatus();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CompleteRequest {
        @NotNull(message = "sellerId는 필수입력값입니다.")
        private Long sellerId;

        @NotNull(message = "productId는 필수입력값입니다.")
        private Long productId;

        @NotNull(message = "orderId는 필수입력값입니다.")
        private Long orderId;

        public OrderCommand.CompleteOrder toCommand() {
            return OrderCommand.CompleteOrder.builder()
                    .sellerId(sellerId)
                    .productId(productId)
                    .orderId(orderId)
                    .build();
        }

        @Builder
        public CompleteRequest(Long sellerId, Long productId, Long orderId) {
            this.sellerId = sellerId;
            this.productId = productId;
            this.orderId = orderId;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ReserveRequest {
        @NotNull(message = "sellerId는 필수입력값입니다.")
        private Long sellerId;

        @NotNull(message = "productId는 필수입력값입니다.")
        private Long productId;

        @NotNull(message = "orderId는 필수입력값입니다.")
        private Long orderId;

        public OrderCommand.ReserveOrder toCommand() {
            return OrderCommand.ReserveOrder.builder()
                    .orderId(orderId)
                    .productId(productId)
                    .sellerId(sellerId)
                    .build();
        }
    }
}
