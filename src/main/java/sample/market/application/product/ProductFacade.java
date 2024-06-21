package sample.market.application.product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.market.domain.product.ProductCommand;
import sample.market.domain.product.ProductInfo;
import sample.market.domain.product.ProductService;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    public ProductInfo registerProduct(ProductCommand.RegisterProduct command) {
        ProductInfo productInfo = productService.registerProduct(command);
        return productInfo;
    }

    public List<ProductInfo> retrievePurchasedProducts(ProductCommand.RetrievePurchaseProduct command) {
        List<ProductInfo> productInfos = productService.retrievePurchasedProducts(command);
        return productInfos;
    }

}
