package sample.market.domain.product;

import java.util.List;

public interface ProductReader {

    Product getProduct(Long orderId);

    List<Product> getProductListByIds(List<Long> productIds);
}
