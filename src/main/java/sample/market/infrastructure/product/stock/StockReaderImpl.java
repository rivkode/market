package sample.market.infrastructure.product.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.market.domain.product.stock.Stock;
import sample.market.domain.product.stock.StockReader;

@Component
@RequiredArgsConstructor
public class StockReaderImpl implements StockReader {

    private final StockRepository stockRepository;

    @Override
    public Stock getStockByProductId(Long productId) {
        return stockRepository.findByProductId(productId);
    }
}
