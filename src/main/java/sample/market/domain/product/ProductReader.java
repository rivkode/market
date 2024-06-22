package sample.market.domain.product;

import java.util.List;

public interface ProductReader {

    Product getProduct(Long orderId);

    List<Product> getProductListByIds(List<Long> productIds);

    List<Product> getReservedProductsBySellerId(Long sellerId);

    List<Product> getReservedProductsByIds(List<Long> productIds);
}
