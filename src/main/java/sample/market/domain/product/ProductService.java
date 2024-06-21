package sample.market.domain.product;

import java.util.List;

public interface ProductService {
    ProductInfo registerProduct(ProductCommand command);

    ProductInfo retrieveProduct(Long productId);

    List<ProductInfo> retrieveProductList(List<Long> productIds);

}
