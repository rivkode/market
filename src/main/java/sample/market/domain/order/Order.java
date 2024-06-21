package sample.market.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import sample.market.common.base.BaseEntity;

@Entity(name = "orders")
@Getter
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyerId")
    private Long buyerId;

    @Column(name = "productId")
    private Long productId;

    private Integer price;

    @Builder
    public Order(Long buyerId, Long productId, Integer price) {
        this.buyerId = buyerId;
        this.productId = productId;
        this.price = price;
    }

    public Order() {

    }

}
