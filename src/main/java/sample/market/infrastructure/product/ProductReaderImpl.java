package sample.market.infrastructure.product;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.market.domain.product.Product;
import sample.market.domain.product.Product.Status;
import sample.market.domain.product.ProductReader;

@Component
@RequiredArgsConstructor
public class ProductReaderImpl implements ProductReader {

    private final ProductRepository productRepository;

    @Override
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<Product> getProductListByIds(List<Long> productIds) {
        return productRepository.findAllByIdIn(productIds);
    }

    @Override
    public List<Product> getReservedProductsBySellerId(Long sellerId) {
        return productRepository.findAllBySellerIdAndStatus(sellerId, Status.RESERVED);
    }
}
