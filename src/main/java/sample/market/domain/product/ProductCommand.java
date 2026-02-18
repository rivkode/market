package sample.market.domain.product;

import lombok.Builder;
import lombok.Getter;

public class ProductCommand {

    @Getter
    @Builder
    public static class RegisterProduct {
        private final String name;
        private final Integer price;
        private final Long sellerId;
        private final Long quantity;

        public Product toEntity() {
            return Product.builder()
                    .name(name)
                    .price(price)
                    .sellerId(sellerId)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RetrieveProducts {
        private final Long buyerId;
        private final Long sellerId;
        private final ProductRetrieveStatus status;

        public void validateCriteria() {
            if (status == null) {
                throw new IllegalArgumentException("status는 필수입력값입니다.");
            }

            if (status == ProductRetrieveStatus.PURCHASED) {
                if (buyerId == null) {
                    throw new IllegalArgumentException("status=PURCHASED 인 경우 buyerId는 필수입니다.");
                }
                if (sellerId != null) {
                    throw new IllegalArgumentException("status=PURCHASED 인 경우 sellerId는 허용되지 않습니다.");
                }
                return;
            }

            if (status == ProductRetrieveStatus.RESERVED) {
                if ((buyerId == null && sellerId == null) || (buyerId != null && sellerId != null)) {
                    throw new IllegalArgumentException("status=RESERVED 인 경우 buyerId 또는 sellerId 중 하나만 입력해야 합니다.");
                }
            }
        }
    }

    @Getter
    @Builder
    public static class RetrievePurchaseProduct {
        private final Long buyerId;

        public RetrievePurchaseProduct toDto() {
            return RetrievePurchaseProduct.builder()
                    .buyerId(buyerId)
                    .build();
        }

    }

    @Getter
    @Builder
    public static class RetrieveReservedProductsBySeller {
        private final Long sellerId;
    }

    @Getter
    @Builder
    public static class RetrieveReservedProductsByBuyer {
        private final Long buyerId;
    }

    @Getter
    @Builder
    public static class PurchaseProduct {
        private final Long buyerId;
    }
}
