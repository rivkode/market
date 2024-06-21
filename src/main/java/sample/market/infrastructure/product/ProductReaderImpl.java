package sample.market.infrastructure.product;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductReader;

@Component
@RequiredArgsConstructor
public class ProductReaderImpl implements ProductReader {

    private final ProductRepository productRepository;

    @Override
    public Product getProduct(Long orderId) {
        return productRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<Product> getProductListByIds(List<Long> productIds) {
        return productRepository.findAllByIdIn(productIds);
    }
}