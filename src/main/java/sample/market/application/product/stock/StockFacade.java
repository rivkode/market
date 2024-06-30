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

    public void decreaseWithRedissonLock(Long productId) {
        RLock lock = redissonClient.getLock(productId.toString());

        try {
            if (!lock.tryLock(10, 1, TimeUnit.SECONDS)) {
                log.info("### redisson getLock timeout");
                return;
            }

            stockService.decrease(productId);
        } catch (final InterruptedException ex) {
            System.out.println("InterruptedException Occur");
            throw new RuntimeException(ex);
        } finally {
            lock.unlock();
        }

    }

    public void decreaseWithOptimistic(Long productId) {
        int maxRetries = 20;
        int retryCount = 0;
        long sleepTime = 50; // 초기 대기 시간 (밀리초)
        while (retryCount < maxRetries) {
            try {
                stockService.decreaseWithOptimistic(productId);
                return; // 성공하면 루프 종료
            } catch (final Exception ex) {
                log.info("### optimistic lock version 충돌, retry count: {}", retryCount);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // 인터럽트 상태를 다시 설정
                    throw new RuntimeException("Thread was interrupted", e);
                }
                retryCount++;
                sleepTime *= 2; // 백오프 시간 증가 (지수 증가)
            }
        }

        throw new RuntimeException("Max retries reached for optimistic locking");

//        while (true) {
//            try {
//                stockService.decreaseWithOptimistic(productId);
//                break;
//            } catch (final Exception ex) {
//                log.info("### optimistic lock version 충돌");
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt(); // 인터럽트 상태를 다시 설정
//                    throw new RuntimeException("Thread was interrupted", e);
//                }
//            }
//        }
    }

    public void decreaseWithPessimistic(Long productId) {
        stockService.decreaseWithPessimistic(productId);
    }
}
