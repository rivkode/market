package sample.market.application.product.stock;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import sample.market.domain.product.stock.StockService;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockFacade {

    private final StockService stockService;
    private final RedissonClient redissonClient;

    public void decreaseWithRedissonLock(Long productId) throws InterruptedException{
        RLock lock = redissonClient.getLock(productId.toString());

        try {
            if (!lock.tryLock(10, 1, TimeUnit.SECONDS)) {
                log.info("### redisson getLock timeout");
                return;
            }

            stockService.decrease(productId);
        } finally {
            lock.unlock();
        }

    }

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
