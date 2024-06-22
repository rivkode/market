package sample.market.infrastructure.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sample.market.domain.product.Product;
import sample.market.domain.product.Product.Status;

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

    /**
     * select *
     * from product
     * where seller_id = sellerId and
     * status = Status. RESERVED
     *
     * @param sellerId
     * @param status
     * @return
     */
    List<Product> findAllBySellerIdAndStatus(Long sellerId, Status status);

    /**
     * select *
     * from product
     * where id in (1, 2, 3 ...) and
     * status = Status. RESERVED
     *
     * @param productIds
     * @param status
     * @return
     */
    List<Product> findAllByIdInAndStatus(List<Long> productIds, Status status);
}
