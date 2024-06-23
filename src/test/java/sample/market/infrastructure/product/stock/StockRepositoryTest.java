package sample.market.infrastructure.product.stock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductStore;
import sample.market.domain.product.stock.Stock;
import sample.market.domain.user.User;
import sample.market.domain.user.UserStore;

@SpringBootTest
@Transactional
class StockRepositoryTest {
    @Autowired
    private UserStore userStore;

    @Autowired
    private ProductStore productStore;

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("productId로 상품의 재고를 조회한다.")
    @Test
    void findByProductId() {
        // given
        User seller = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        User buyer = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        userStore.store(seller);
        userStore.store(buyer);

        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(seller.getId())
                .build();
        product1.reserved();
        productStore.store(product1);

        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(100L)
                .build();
        stockRepository.save(stock);

        // when
        Stock getStock = stockRepository.findByProductId(product1.getId());

        // then
        assertThat(getStock.getProductId()).isEqualTo(product1.getId());
        assertThat(getStock.getQuantity()).isEqualTo(stock.getQuantity());

    }

    @DisplayName("비관적 락 - productId로 상품의 재고를 조회한다.")
    @Test
    void findByIdWithPessimisticLock() {
        // given
        User seller = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        User buyer = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        userStore.store(seller);
        userStore.store(buyer);

        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(seller.getId())
                .build();
        product1.reserved();
        productStore.store(product1);

        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(100L)
                .build();
        stockRepository.save(stock);

        // when
        Stock getStock = stockRepository.findByIdWithPessimisticLock(product1.getId());

        // then
        assertThat(getStock.getProductId()).isEqualTo(product1.getId());
        assertThat(getStock.getQuantity()).isEqualTo(stock.getQuantity());
    }

    @DisplayName("낙관적 락 - productId로 상품의 재고를 조회한다.")
    @Test
    void findByIdWithOptimisticLock() {
        // given
        User seller = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        User buyer = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        userStore.store(seller);
        userStore.store(buyer);

        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(seller.getId())
                .build();
        product1.reserved();
        productStore.store(product1);

        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(100L)
                .build();
        stockRepository.save(stock);

        // when
        Stock getStock = stockRepository.findByIdWithOptimisticLock(product1.getId());

        // then
        assertThat(getStock.getProductId()).isEqualTo(product1.getId());
        assertThat(getStock.getQuantity()).isEqualTo(stock.getQuantity());
    }



}