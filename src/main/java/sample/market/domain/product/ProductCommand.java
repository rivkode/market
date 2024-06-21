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
    public static class RetrievePurchaseProduct {
        private final Long buyerId;

        public RetrievePurchaseProduct toDto() {
            return RetrievePurchaseProduct.builder()
                    .buyerId(buyerId)
                    .build();
        }

    }

}
