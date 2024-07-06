package sample.market.infrastructure.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.market.domain.stock.Stock;
import sample.market.domain.stock.StockReader;

@Component
@RequiredArgsConstructor
public class StockReaderImpl implements StockReader {

    private final StockRepository stockRepository;

    @Override
    public Stock getStockByProductId(Long productId) {
        return stockRepository.findByProductId(productId);
    }
}
