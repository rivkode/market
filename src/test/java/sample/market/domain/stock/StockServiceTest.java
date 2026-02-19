package sample.market.domain.stock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductStore;
import sample.market.domain.user.User;
import sample.market.domain.user.UserStore;
import sample.market.infrastructure.stock.StockRepository;

@SpringBootTest
class StockServiceTest {
    @Autowired
    private UserStore userStore;

    @Autowired
    private ProductStore productStore;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockService stockService;

    @DisplayName("비관적 락 - productId로 조회한 상품의 재고를 1 감소시킨다.")
    @Test
    void decreaseWithPessimistic() {
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

        Long quantity = 100L;
        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(quantity)
                .build();
        Stock savedStock = stockRepository.save(stock);

        // when
        stockService.decreaseWithPessimistic(product1.getId());
        Stock getStock = stockRepository.findById(savedStock.getId()).orElseThrow();


        // then
        assertThat(getStock.getQuantity()).isEqualTo(quantity - 1);
    }

    @DisplayName("낙관적 락 - productId로 조회한 상품의 재고를 1 감소시킨다.")
    @Test
    void decreaseWithOptimistic() throws InterruptedException {
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

        Long quantity = 100L;
        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(quantity)
                .build();
        Stock savedStock = stockRepository.save(stock);

        // when
        stockService.decreaseWithOptimistic(product1.getId());
        Stock getStock = stockRepository.findById(savedStock.getId()).orElseThrow();

        // then
        assertThat(getStock.getQuantity()).isEqualTo(quantity - 1);
    }
}