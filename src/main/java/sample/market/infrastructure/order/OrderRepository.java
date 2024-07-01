package sample.market.infrastructure.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sample.market.domain.order.Order;
import sample.market.domain.order.Order.Status;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * select *
     * from order
     * where buyerId = buyerId and
     * status = Status.ORDER_COMPLETE
     *
     * @param buyerId
     * @param status
     * @return
     */
    List<Order> findByBuyerIdAndStatus(Long buyerId, Status status);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM orders o WHERE o.productId = :productId AND o.status != 'ORDER_COMPLETE'")
    Boolean existsByProductIdAndStatusNotOrderComplete(Long productId);
}
