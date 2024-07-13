package sample.market.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sample.market.common.base.BaseEntity;

@Entity(name = "orders")
@Getter
@Table(indexes = {@Index(name = "buyer_id_idx", columnList = "buyerId")})
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_id")
    private Long buyerId;

    @Column(name = "product_id")
    private Long productId;

    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        INIT("거래_시작"),
        ORDER_CANCEL("거래_취소"),
        ORDER_SALE_APPROVED("거래_판매_승인"),
        ORDER_RESERVE("거래_예약"),
        ORDER_COMPLETE("거래_완료");

        private final String value;
    }

    @Builder
    public Order(Long buyerId, Long productId, Integer price) {
        this.buyerId = buyerId;
        this.productId = productId;
        this.price = price;
        this.status = Status.INIT;
    }

    public void complete() {
        this.status = Status.ORDER_COMPLETE;
    }

    public void cancel() {
        this.status = Status.ORDER_CANCEL;
    }

    public void approve() {
        this.status = Status.ORDER_SALE_APPROVED;
    }

    public void reserve() {
        this.status = Status.ORDER_RESERVE;
    }

    public Order() {

    }

}
