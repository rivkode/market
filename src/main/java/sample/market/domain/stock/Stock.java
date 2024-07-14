package sample.market.domain.stock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@Table(indexes = {@Index(name = "product_id_idx", columnList = "productId")})
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Version
    private Long version;

    @Column(name = "quantity")
    private Long quantity;

    @Builder
    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Stock() {

    }

    public void decrease() {
        log.info("stock start decrease quantity : " + this.quantity);
        if (this.quantity <= 0) {
            throw new RuntimeException("상품의 재고는 0 미만이 될 수 없습니다.");
        }

        this.quantity -= 1;
        log.info("stock after decrease quantity : " + this.quantity);
    }
}
