package sample.market.application.product.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sample.market.domain.product.stock.StockService;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockFacade {

    private final StockService stockService;

    public void decreaseWithOptimistic(Long productId) throws InterruptedException {
        while (true) {
            try {
                stockService.decreaseWithOptimistic(productId);
                break;
            } catch (final Exception ex) {
                log.info("### optimistic lock version 충돌");
                Thread.sleep(50);
            }
        }
    }

    public void decreaseWithPessimistic(Long productId) {
        stockService.decreaseWithPessimistic(productId);
    }
}
