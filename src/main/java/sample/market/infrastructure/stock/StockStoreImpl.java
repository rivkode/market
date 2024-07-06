package sample.market.infrastructure.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sample.market.domain.stock.Stock;
import sample.market.domain.stock.StockStore;

@Component
@RequiredArgsConstructor
public class StockStoreImpl implements StockStore {

    private final StockRepository stockRepository;

    @Override
    public Stock store(Stock stock) {
        return stockRepository.save(stock);
    }
}
