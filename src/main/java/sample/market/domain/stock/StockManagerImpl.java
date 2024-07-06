package sample.market.domain.stock;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class StockManagerImpl implements StockManager {

    private final StockStore stockStore;

    @Override
    @Transactional
    public void registerStock(Long productId, Long quantity) {
        Stock stock = Stock.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        stockStore.store(stock);
    }
}
