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

    @DisplayName("구매 상품 조회시 구매시 가격을 나타냅니다.")
    @Test
    void retrievePurchasedProducts() {
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
                .username("buyer")
                .build();
        userStore.store(seller);
        userStore.store(buyer);

        int originalPrice = 1000;
        int changePrice = 2000;

        Product product1 = Product.builder()
                .price(originalPrice)
                .name("마스크")
                .sellerId(seller.getId())
                .build();
        product1.reserved();
        productStore.store(product1);

        Order order = Order.builder()
                .price(originalPrice)
                .productId(product1.getId())
                .buyerId(buyer.getId())
                .build();
        order.complete();
        orderStore.store(order);

        ProductCommand.RetrievePurchaseProduct command = ProductCommand.RetrievePurchaseProduct.builder()
                .buyerId(buyer.getId())
                .build();

        // when
        product1.changePrice(changePrice);
        productStore.store(product1);

        List<ProductInfo> productInfos = productService.retrievePurchasedProducts(command);

        // then
        assertThat(productInfos).hasSize(1)
                .extracting("name", "purchasePrice")
                .containsExactlyInAnyOrder(
                        tuple(product1.getName(), originalPrice)
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

        int originalPrice = 1000;
        int changePrice = 2000;

        Product product1 = Product.builder()
                .price(originalPrice)
                .name("마스크")
                .sellerId(seller.getId())
                .build();

        Product product2 = Product.builder()
                .price(originalPrice)
                .name("충전기")
                .sellerId(seller.getId())
                .build();
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
        order1.reserve();
        order2.reserve();
        orderStore.store(order1);
        orderStore.store(order2);

        ProductCommand.RetrieveReservedProductsByBuyer command = ProductCommand.RetrieveReservedProductsByBuyer.builder()
                .buyerId(buyer.getId())
                .build();

        // when
        product1.changePrice(changePrice);
        productStore.store(product1);

        List<ProductInfo> productInfos = productService.retrieveReservedProductsByBuyer(command);

        // then
        assertThat(productInfos).hasSize(2)
                .extracting("name", "purchasePrice")
                .containsExactlyInAnyOrder(
                        tuple(product1.getName(), originalPrice),
                        tuple(product2.getName(), originalPrice)
                );
    }




}