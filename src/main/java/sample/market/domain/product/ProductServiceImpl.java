package sample.market.domain.product;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.market.domain.order.Order;
import sample.market.domain.order.OrderService;
import sample.market.domain.product.ProductCommand.RetrievePurchaseProduct;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductStore productStore;
    private final ProductReader productReader;
    private final ProductInfoMapper productInfoMapper;
    private final OrderService orderService;

    @Override
    public ProductInfo registerProduct(ProductCommand.RegisterProduct command) {
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

    @Override
    public List<ProductInfo> retrievePurchasedProducts(RetrievePurchaseProduct command) {
        List<Order> orders = orderService.retrieveCompletedOrders(command);
        List<Long> productIds = orders.stream()
                .map(Order::getProductId)
                .collect(Collectors.toList());
        List<ProductInfo> productInfos = retrieveProductList(productIds);

        return productInfos;
    }
}
