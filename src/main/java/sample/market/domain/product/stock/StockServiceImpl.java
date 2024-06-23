package sample.market.domain.product.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.market.infrastructure.product.stock.StockRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;

    // PESSIMISTIC
    @Transactional
    public void decreaseWithPessimistic(Long productId) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(productId);
        log.info("start stock decrease");
        try {
            stock.decrease();
        } catch (Exception e) {
            log.error("Exception : " + e.getMessage());
        }
        log.info("success stock decrease");
    }

    // OPTIMISTIC
    @Transactional
    public void decreaseWithOptimistic(Long productId) throws InterruptedException {
        while (true) {
            try {
                Stock stock = stockRepository.findByIdWithOptimisticLock(productId);
                log.info("start stock decrease");
                stock.decrease();
                log.info("success stock decrease");
                break;
            } catch (Exception e) {
                log.error("Exception : " + e.getMessage());
                Thread.sleep(100); // 예외 발생 시 잠시 대기 후 재시도
            }
        }
    }

}
