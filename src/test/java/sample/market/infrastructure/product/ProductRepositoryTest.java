package sample.market.infrastructure.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.market.domain.product.Product;
import sample.market.domain.product.Product.Status;
import sample.market.domain.product.ProductStore;
import sample.market.domain.user.User;
import sample.market.domain.user.UserStore;

@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    private UserStore userStore;

    @Autowired
    private ProductStore productStore;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("상품 id 리스트로 상품을 조회한다.")
    @Test
    void findAllByIdIn() {
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
        Product savedProduct = productStore.store(product1);

        // when
        List<Product> products = productRepository.findAllByIdIn(List.of(savedProduct.getId()));

        // then
        assertThat(products).hasSize(1)
                .extracting("sellerId", "name", "price")
                .containsExactlyInAnyOrder(
                        tuple(seller.getId(), product1.getName(), product1.getPrice())
                );
    }

    @DisplayName("sellerId와 status로 상품을 조회한다.")
    @Test
    void findAllBySellerIdAndStatus() {
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


        // when
        List<Product> products = productRepository.findAllBySellerIdAndStatus(seller.getId(),
                Status.RESERVED);

        // then
        assertThat(products).hasSize(1)
                .extracting("sellerId", "name", "price")
                .containsExactlyInAnyOrder(
                        tuple(seller.getId(), product1.getName(), product1.getPrice())
                );

    }

}