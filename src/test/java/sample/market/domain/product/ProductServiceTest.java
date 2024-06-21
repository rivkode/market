package sample.market.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.market.domain.product.Product.Status;
import sample.market.domain.user.User;
import sample.market.domain.user.UserStore;

@SpringBootTest
@Transactional
class ProductServiceTest {
    @Autowired
    private ProductStore productStore;

    @Autowired
    private UserStore userStore;

    @Autowired
    private ProductService productService;

    @DisplayName("등록된 제품에는 상세조회시에 예약상태를 포함한다.")
    @Test
    void getProductStatus() {
        // given
        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        userStore.store(user);

        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(user.getId())
                .build();
        productStore.store(product1);


        // when
        ProductInfo getProduct = productService.retrieveProduct(product1.getId());

        // then
        assertThat(getProduct.getStatus()).isEqualTo(Status.PREPARE);
    }


    @DisplayName("등록된 제품에는 목록조회시에 예약상태를 포함한다.")
    @Test
    void getProductListStatus() {
        // given
        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        userStore.store(user);

        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(user.getId())
                .build();
        Product product2 = Product.builder()
                .price(3000)
                .name("충전기")
                .sellerId(user.getId())
                .build();
        productStore.storeAll(List.of(product1, product2));


        // when
        List<ProductInfo> getProductList = productService.retrieveProductList(List.of(product1.getId(), product2.getId()));

        // then
        assertThat(getProductList).hasSize(2)
                .extracting("status", "price", "name")
                .containsExactlyInAnyOrder(
                        tuple(Status.PREPARE, 1000, "마스크"),
                        tuple(Status.PREPARE, 3000, "충전기")
                );

    }

}