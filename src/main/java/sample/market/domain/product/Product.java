package sample.market.domain.product;

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
import sample.market.common.util.TokenGenerator;

@Entity
@Getter
@Table(indexes = {@Index(name = "seller_id_idx", columnList = "sellerId")})
public class Product extends BaseEntity {

    private static final String PRODUCT_PREFIX = "prd_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productToken;

    @Column(name = "sellerId")
    private Long sellerId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        PREPARE("준비중"),
        ON_SALE("판매중"),
        RESERVED("예약중"),
        END_OF_SALE("완료");

        private final String value;
    }

    @Builder
    public Product(String name, Long sellerId, Integer price) {
        if (name == null || name.length() == 0) throw new IllegalArgumentException("empty product name");
        if (sellerId == null || sellerId <= 0) throw new IllegalArgumentException("invalid sellerId");
        if (price == null || price <= 0) throw new IllegalArgumentException("invalid price");

        this.productToken = TokenGenerator.randomCharacterWithPrefix(PRODUCT_PREFIX);
        this.name = name;
        this.sellerId = sellerId;
        this.price = price;
        this.status = Status.PREPARE;
    }

    public void onSale() {
        this.status = Status.ON_SALE;
    }

    public void reserved() {
        this.status = Status.RESERVED;
    }

    public void completed() {
        this.status = Status.END_OF_SALE;
    }

    protected Product() {

    }

}

