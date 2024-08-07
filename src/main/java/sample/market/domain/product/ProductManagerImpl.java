package sample.market.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sample.market.domain.order.OrderReader;
import sample.market.domain.stock.Stock;
import sample.market.domain.stock.StockReader;

@Component
@RequiredArgsConstructor
public class ProductManagerImpl implements ProductManager {

    private final ProductReader productReader;
    private final StockReader stockReader;
    private final OrderReader orderReader;

    @Override
    @Transactional
    public void updateProductStatus(Long productId) {
        Product product = productReader.getProduct(productId);
        Stock stock = stockReader.getStockByProductId(productId);
        Long productQuantity = stock.getQuantity();

        boolean existOrderComplete = orderReader.existsByProductIdAndStatusNotOrderComplete(product.getId());

        //만약 남은 수량이 0개이며 구매확정이 모두 되었다면 Product 의 상태는 완료 아니라면 예약중
        if ((productQuantity == 0L)) {
            if (existOrderComplete) {
                product.reserved();
            } else {
                product.completed();
            }
        }

    }
}
