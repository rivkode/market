package sample.market.domain.product;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductStore productStore;
    private final ProductReader productReader;
    private final ProductInfoMapper productInfoMapper;

    @Override
    public ProductInfo registerProduct(ProductCommand command) {
        Product initProduct = command.toEntity();
        Product product = productStore.store(initProduct);
        return new ProductInfo(product);
    }

    @Override
    public ProductInfo retrieveProduct(Long productId) {
        Product product = productReader.getProduct(productId);
        return new ProductInfo(product);
    }

    @Override
    public List<ProductInfo> retrieveProductList(List<Long> productIds) {
        List<Product> products = productReader.getProductListByIds(productIds);
        return productInfoMapper.of(products);
    }
}
