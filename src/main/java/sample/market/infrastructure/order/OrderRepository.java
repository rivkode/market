package sample.market.infrastructure.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
