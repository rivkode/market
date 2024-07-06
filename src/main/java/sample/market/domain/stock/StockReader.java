package sample.market.domain.stock;

public interface StockReader {

    Stock getStockByProductId(Long productId);

}
