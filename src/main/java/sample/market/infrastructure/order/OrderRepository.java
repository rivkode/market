package sample.market.infrastructure.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sample.market.domain.order.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
