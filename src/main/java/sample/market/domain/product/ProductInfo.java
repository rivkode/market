package sample.market.domain.product;

import lombok.Builder;
import lombok.Getter;
import sample.market.domain.product.Product.Status;

@Getter
public class ProductInfo {
    private final Long productId;
    private final String name;
    private final Integer price;
    private final Integer purchasePrice;
    private final Status status;
    private final Long sellerId;


    public ProductInfo(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.purchasePrice = product.getPrice();
        this.status = product.getStatus();
        this.sellerId = product.getSellerId();
    }

    @Builder
    public ProductInfo(Product product, Integer purchasePrice) {
        this.productId = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.purchasePrice = purchasePrice;
        this.status = product.getStatus();
        this.sellerId = product.getSellerId();
    }


}
