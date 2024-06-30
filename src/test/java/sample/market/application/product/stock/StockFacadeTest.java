package sample.market.application.product.stock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductStore;
import sample.market.domain.product.stock.Stock;
import sample.market.domain.user.User;
import sample.market.domain.user.UserStore;
import sample.market.infrastructure.product.stock.StockRepository;

/**
 * 동시성 테스트 시
 * countDownLatch 이 .countDown()을 통해 1씩 감소하며 0이 될때까지 스레드가 기다린다.
 * 0이 되면 동시에 stockFacade.decrease()를 실행시킨다.
 * await() 메소드는 count가 0이 될 때까지 대기하는 역할을 한다.
 * 이 동시에 실행시키는 과정이 끝날때까지 기다리기 위해 .await()가 그 시간을 보장해준다.
 * 즉, 스레드 작업이 스레드 개수(100)만큼 실행되기 전까지 대기하고, 300개가 실행되어 count가 0이 되면 이후 로직 실행한다.
 */

@SpringBootTest
class StockFacadeTest {
    @Autowired
    private UserStore userStore;

    @Autowired
    private ProductStore productStore;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockFacade stockFacade;

    @DisplayName("비관적 락 - 동시에 300개의 요청으로 productId로 조회한 상품의 재고를 1씩 감소시킨다.")
    @Test
    void decreaseWithPessimistic() throws InterruptedException {
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

        Long quantity = 300L;
        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(quantity)
                .build();
        Stock savedStock = stockRepository.save(stock);

        int threadCount = 300;

        final ExecutorService executorService = Executors.newFixedThreadPool(100);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
                    try {
                        stockFacade.decreaseWithPessimistic(product1.getId());
                    } finally {
                        countDownLatch.countDown();
                    }
                }
        ));

        countDownLatch.await();

        Stock getStock = stockRepository.findById(savedStock.getId()).orElseThrow();

        // then
        assertThat(getStock.getQuantity()).isZero();
    }



    @DisplayName("낙관적 락 - 동시에 300개의 요청으로 productId로 조회한 상품의 재고를 1씩 감소시킨다.")
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

        Long quantity = 300L;
        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(quantity)
                .build();
        Stock savedStock = stockRepository.save(stock);

        int threadCount = 300;

        final ExecutorService executorService = Executors.newFixedThreadPool(100);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
                    try {
                        stockFacade.decreaseWithOptimistic(product1.getId());
                    } finally {
                        countDownLatch.countDown();
                    }
                }
        ));

        countDownLatch.await();

        Stock getStock = stockRepository.findById(savedStock.getId()).orElseThrow();

        // then
        assertThat(getStock.getQuantity()).isZero();
    }

    @DisplayName("Reddision Lock - 동시에 300개의 요청으로 productId로 조회한 상품의 재고를 1씩 감소시킨다.")
    @Test
    void decreaseWithRedissonLock() throws InterruptedException {
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

        Long quantity = 300L;
        Stock stock = Stock.builder()
                .productId(product1.getId())
                .quantity(quantity)
                .build();
        Stock savedStock = stockRepository.save(stock);

        int threadCount = 300;

        final ExecutorService executorService = Executors.newFixedThreadPool(100);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
                    try {
                        stockFacade.decreaseWithRedissonLock(product1.getId());
                    } finally {
                        countDownLatch.countDown();
                    }
                }
        ));

        countDownLatch.await();

        Stock getStock = stockRepository.findById(savedStock.getId()).orElseThrow();

        // then
        assertThat(getStock.getQuantity()).isZero();
    }


}