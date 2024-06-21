package sample.market.infrastructure.product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductStore;

@Component
@RequiredArgsConstructor
public class ProductStoreImpl implements ProductStore {

    private final ProductRepository productRepository;
    @Override
    public Product store(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void storeAll(List<Product> products) {
        productRepository.saveAll(products);
    }
}
