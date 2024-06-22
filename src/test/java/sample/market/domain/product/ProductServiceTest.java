package sample.market.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sample.market.domain.order.Order;
import sample.market.domain.order.OrderStore;
import sample.market.domain.product.Product.Status;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsByBuyer;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsBySeller;
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

    @Autowired
    private OrderStore orderStore;

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

    @DisplayName("상품 등록시 등록한 정보가 반환된다.")
    @Test
    void register() {
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

        ProductCommand.RegisterProduct command = ProductCommand.RegisterProduct.builder()
                .sellerId(user.getId())
                .name(product1.getName())
                .price(product1.getPrice())
                .build();

        // when
        ProductInfo productInfo = productService.registerProduct(command);

        // then
        assertThat(productInfo.getSellerId()).isEqualTo(user.getId());
        assertThat(productInfo.getStatus()).isEqualTo(product1.getStatus());
        assertThat(productInfo.getName()).isEqualTo(product1.getName());
        assertThat(productInfo.getPrice()).isEqualTo(product1.getPrice());
    }

    @DisplayName("sellerId와 status로 상품을 조회한다.")
    @Test
    void retrieveReservedProductsBySeller() {
        // given
        User seller = User.builder()
                .email("email@email.com")
                .password("password")
                .role("user")
                .username("username")
                .build();
        userStore.store(seller);

        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(seller.getId())
                .build();
        product1.reserved();
        productStore.store(product1);

        ProductCommand.RetrieveReservedProductsBySeller command = ProductCommand.RetrieveReservedProductsBySeller.builder()
                .sellerId(seller.getId())
                .build();

        // when
        List<ProductInfo> productInfos = productService.retrieveReservedProductsBySeller(command);

        // then
        assertThat(productInfos).hasSize(1)
                .extracting("sellerId", "name", "price", "status")
                .containsExactlyInAnyOrder(
                        tuple(seller.getId(), product1.getName(), product1.getPrice(), product1.getStatus())
                );
    }

    @DisplayName("구매자는 예약된 상품을 조회합니다.")
    @Test
    void retrieveReservedProductsByBuyer() {
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

        Product product2 = Product.builder()
                .price(3000)
                .name("충전기")
                .sellerId(seller.getId())
                .build();
        product1.reserved();
        product2.reserved();
        productStore.storeAll(List.of(product1, product2));

        Order order1 = Order.builder()
                .buyerId(buyer.getId())
                .productId(product1.getId())
                .price(product1.getPrice())
                .build();

        Order order2 = Order.builder()
                .buyerId(buyer.getId())
                .productId(product2.getId())
                .price(product2.getPrice())
                .build();
        orderStore.store(order1);
        orderStore.store(order2);

        ProductCommand.RetrieveReservedProductsByBuyer command = ProductCommand.RetrieveReservedProductsByBuyer.builder()
                .buyerId(buyer.getId())
                .build();

        // when
        List<ProductInfo> productInfos = productService.retrieveReservedProductsByBuyer(command);

        // then
        assertThat(productInfos).hasSize(2)
                .extracting("sellerId", "name", "price", "status")
                .containsExactlyInAnyOrder(
                        tuple(seller.getId(), product1.getName(), product1.getPrice(), product1.getStatus()),
                        tuple(seller.getId(), product2.getName(), product2.getPrice(), product2.getStatus())
                );
    }




}