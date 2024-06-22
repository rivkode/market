package sample.market.domain.product;

import java.util.List;
import sample.market.domain.product.ProductCommand.RetrievePurchaseProduct;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsBySeller;

public interface ProductService {
    ProductInfo registerProduct(ProductCommand.RegisterProduct command);

    ProductInfo retrieveProduct(Long productId);

    List<ProductInfo> retrieveProductList(List<Long> productIds);

    List<ProductInfo> retrievePurchasedProducts(RetrievePurchaseProduct command);

    List<ProductInfo> retrieveReservedProductsBySeller(RetrieveReservedProductsBySeller command);
}
