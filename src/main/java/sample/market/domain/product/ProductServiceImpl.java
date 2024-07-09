package sample.market.domain.product;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sample.market.domain.order.Order;
import sample.market.domain.order.OrderReader;
import sample.market.domain.product.ProductCommand.RetrievePurchaseProduct;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsByBuyer;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsBySeller;
import sample.market.domain.stock.StockManager;
import sample.market.domain.stock.StockStore;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductStore productStore;
    private final ProductReader productReader;
    private final ProductInfoMapper productInfoMapper;
    private final StockManager stockManager;
    private final OrderReader orderReader;

    @Override
    public ProductInfo registerProduct(ProductCommand.RegisterProduct command) {
        Product initProduct = command.toEntity();
        Product product = productStore.store(initProduct);
        stockManager.registerStock(product.getId(), command.getQuantity());
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
        List<Order> orders = orderReader.getCompletedProducts(command.getBuyerId());
        List<Long> productIds = orders.stream()
                .map(Order::getProductId)
                .collect(Collectors.toList());
        List<ProductInfo> productInfos = retrieveProductList(productIds);

        return productInfos;
    }

    @Override
    public List<ProductInfo> retrieveReservedProductsBySeller(
            RetrieveReservedProductsBySeller command) {
        // 구매할 당시의 가격 정보
        List<Product> products = productReader.getReservedProductsBySellerId(command.getSellerId());
        return productInfoMapper.of(products);
    }

    @Override
    public List<ProductInfo> retrieveReservedProductsByBuyer(RetrieveReservedProductsByBuyer command) {
        // 구매할 당시의 가격 정보
        List<Order> orders = orderReader.getInitProducts(command.getBuyerId());
        List<Long> productIds = orders.stream()
                .map(Order::getProductId)
                .toList();
        List<Product> products = productReader.getReservedProductsByIds(productIds);
        return productInfoMapper.of(products);
    }
}
