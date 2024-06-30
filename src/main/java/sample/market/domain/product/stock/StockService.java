package sample.market.domain.product.stock;


public interface StockService {
    void decreaseWithPessimistic(Long productId);

    void decreaseWithOptimistic(Long productId) throws InterruptedException;

    void decrease(Long productId);
}
