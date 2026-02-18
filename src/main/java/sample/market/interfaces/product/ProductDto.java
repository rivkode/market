package sample.market.interfaces.product;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.market.domain.product.Product.Status;
import sample.market.domain.product.ProductCommand;
import sample.market.domain.product.ProductInfo;
import sample.market.domain.product.ProductRetrieveStatus;

public class ProductDto {
    @Getter
    @NoArgsConstructor
    public static class RegisterRequest {
        @NotEmpty(message = "name은 필수입력값입니다.")
        private String name;

        @NotNull(message = "price는 필수입력값입니다.")
        private Integer price;

        @NotNull(message = "sellerId는 필수입력값입니다.")
        private Long sellerId;



        public ProductCommand.RegisterProduct toCommand() {
            return ProductCommand.RegisterProduct.builder()
                    .sellerId(sellerId)
                    .name(name)
                    .price(price)
                    .build();
        }
        @Builder
        public RegisterRequest(String name, Integer price, Long sellerId) {
            this.name = name;
            this.price = price;
            this.sellerId = sellerId;
        }
    }

    @Getter
    public static class RegisterResponse {
        private Long productId;

        public RegisterResponse(ProductInfo productInfo) {
            this.productId = productInfo.getProductId();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RetrievePurchasedRequest {
        @NotNull(message = "buyerId는 필수입력값입니다.")
        private Long buyerId;

        public ProductCommand.RetrievePurchaseProduct toCommand() {
            return ProductCommand.RetrievePurchaseProduct.builder()
                    .buyerId(buyerId)
                    .build();

        }

        @Builder
        public RetrievePurchasedRequest(Long buyerId) {
            this.buyerId = buyerId;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RetrieveRequest {
        private Long buyerId;
        private Long sellerId;

        @NotNull(message = "status는 필수입력값입니다.")
        private ProductRetrieveStatus status;

        public ProductCommand.RetrieveProducts toRetrieveCommand() {
            return ProductCommand.RetrieveProducts.builder()
                    .buyerId(buyerId)
                    .sellerId(sellerId)
                    .status(status)
                    .build();
        }

        @Builder
        public RetrieveRequest(Long buyerId, Long sellerId, ProductRetrieveStatus status) {
            this.buyerId = buyerId;
            this.sellerId = sellerId;
            this.status = status;
        }
    }

    @Getter
    @NoArgsConstructor
    public class RetrieveReservedRequestWithSeller {
        @NotNull(message = "sellerId는 필수입력값입니다.")
        private Long sellerId;

        public ProductCommand.RetrieveReservedProductsBySeller toCommand() {
            return ProductCommand.RetrieveReservedProductsBySeller.builder()
                    .sellerId(sellerId)
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor
    public class RetrieveReservedRequestWithBuyer {
        @NotNull(message = "sellerId는 필수입력값입니다.")
        private Long buyerId;

        public ProductCommand.RetrieveReservedProductsByBuyer toCommand() {
            return ProductCommand.RetrieveReservedProductsByBuyer.builder()
                    .buyerId(buyerId)
                    .build();
        }
    }


    @Getter
    public static class RetrieveResponse {

        private String name;
        private Integer price;
        private Status status;
        private List<ProductInfo> productInfos; // List 필드 추가


        @Builder
        public RetrieveResponse(ProductInfo productInfo) {
            this.name = productInfo.getName();
            this.price = productInfo.getPrice();
            this.status = productInfo.getStatus();
        }

        public RetrieveResponse(List<ProductInfo> productInfos) {
            this.productInfos = productInfos;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PurchaseRequest {
        @NotNull(message = "buyerId는 필수입력값입니다.")
        private Long buyerId;


        public ProductCommand.PurchaseProduct toCommand() {
            return ProductCommand.PurchaseProduct.builder()
                    .buyerId(buyerId)
                    .build();

        }

    }

    @Getter
    public static class PurchaseResponse {
        private String name;
        private Integer price;
        private Status status;

        public PurchaseResponse(ProductInfo productInfo) {
            this.name = productInfo.getName();
            this.price = productInfo.getPrice();
            this.status = productInfo.getStatus();
        }

    }


}
