package sample.market.application.product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.market.domain.product.ProductCommand.*;
import sample.market.domain.product.ProductInfo;
import sample.market.domain.product.ProductRetrieveStatus;
import sample.market.domain.product.ProductService;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    public ProductInfo registerProduct(RegisterProduct command) {
        ProductInfo productInfo = productService.registerProduct(command);
        return productInfo;
    }

    public List<ProductInfo> retrievePurchasedProducts(RetrievePurchaseProduct command) {
        List<ProductInfo> productInfos = productService.retrievePurchasedProducts(command);
        return productInfos;
    }

    public List<ProductInfo> retrieveReservedProductsBySeller(RetrieveReservedProductsBySeller command) {
        List<ProductInfo> productInfos = productService.retrieveReservedProductsBySeller(command);
        return productInfos;
    }

    public List<ProductInfo> retrieveReservedProductsByBuyer(RetrieveReservedProductsByBuyer command) {
        List<ProductInfo> productInfos = productService.retrieveReservedProductsByBuyer(command);
        return productInfos;
    }

    public List<ProductInfo> retrieveProducts(RetrieveProducts command) {
        command.validateCriteria();

        if (command.getStatus() == ProductRetrieveStatus.PURCHASED) {
            RetrievePurchaseProduct purchaseCommand = RetrievePurchaseProduct.builder()
                    .buyerId(command.getBuyerId())
                    .build();
            return productService.retrievePurchasedProducts(purchaseCommand);
        }

        if (command.getSellerId() != null) {
            RetrieveReservedProductsBySeller sellerCommand = RetrieveReservedProductsBySeller.builder()
                    .sellerId(command.getSellerId())
                    .build();
            return productService.retrieveReservedProductsBySeller(sellerCommand);
        }

        RetrieveReservedProductsByBuyer buyerCommand = RetrieveReservedProductsByBuyer.builder()
                .buyerId(command.getBuyerId())
                .build();
        return productService.retrieveReservedProductsByBuyer(buyerCommand);
    }
}
