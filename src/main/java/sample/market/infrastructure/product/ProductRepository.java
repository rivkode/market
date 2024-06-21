package sample.market.infrastructure.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sample.market.domain.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * select *
     * from product
     * where id in (1, 2, 3, ...);
     *
     * @param productIds
     * @return
     */
    List<Product> findAllByIdIn(List<Long> productIds);
}
