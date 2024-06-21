package sample.market.domain.product;

import java.util.List;

public interface ProductStore {

    Product store(Product product);

    void storeAll(List<Product> products);

}
