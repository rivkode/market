package sample.market.domain.stock;


public interface StockService {
    void decreaseWithPessimistic(Long productId);

    void decreaseWithOptimistic(Long productId) throws InterruptedException;

    void decrease(Long productId);
}
