package sample.market.domain.product;

import lombok.Getter;
import sample.market.domain.product.Product.Status;

@Getter
public class ProductInfo {
    private final Long productId;
    private final String name;
    private final Integer price;
    private final Status status;
    private final Long sellerId;


    public ProductInfo(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.status = product.getStatus();
        this.sellerId = product.getSellerId();
    }


}
