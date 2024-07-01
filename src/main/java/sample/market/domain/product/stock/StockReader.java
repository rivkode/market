package sample.market.domain.product.stock;

public interface StockReader {

    Stock getStockByProductId(Long productId);

}
