package sample.market.domain.product;

import java.util.List;
import sample.market.domain.product.ProductCommand.RetrievePurchaseProduct;

public interface ProductService {
    ProductInfo registerProduct(ProductCommand.RegisterProduct command);

    ProductInfo retrieveProduct(Long productId);

    List<ProductInfo> retrieveProductList(List<Long> productIds);

    List<ProductInfo> retrievePurchasedProducts(RetrievePurchaseProduct command);
}
