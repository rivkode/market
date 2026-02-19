package sample.market.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.market.domain.order.Order;
import sample.market.domain.order.OrderReader;
import sample.market.domain.product.Product.Status;
import sample.market.domain.product.ProductCommand.RetrieveReservedProductsByBuyer;
import sample.market.domain.stock.StockManager;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductStore productStore;
    @Mock
    private ProductReader productReader;
    @Mock
    private ProductInfoMapper productInfoMapper;
    @Mock
    private StockManager stockManager;
    @Mock
    private OrderReader orderReader;

    @InjectMocks
    private ProductServiceImpl productService;

    @DisplayName("등록된 제품에는 상세조회시에 예약상태를 포함한다.")
    @Test
    void getProductStatus() {
        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(1L)
                .build();
        when(productReader.getProduct(1L)).thenReturn(product1);

        ProductInfo getProduct = productService.retrieveProduct(1L);

        assertThat(getProduct.getStatus()).isEqualTo(Status.PREPARE);
    }

    @DisplayName("등록된 제품에는 목록조회시에 예약상태를 포함한다.")
    @Test
    void getProductListStatus() {
        Product product1 = Product.builder()
                .price(1000)
                .name("마스크")
                .sellerId(1L)
                .build();
        Product product2 = Product.builder()
                .price(3000)
                .name("충전기")
                .sellerId(1L)
                .build();
        List<Product> products = List.of(product1, product2);
        List<ProductInfo> infos = List.of(new ProductInfo(product1), new ProductInfo(product2));

        when(productReader.getProductListByIds(List.of(1L, 2L))).thenReturn(products);
        when(productInfoMapper.of(products)).thenReturn(infos);

        List<ProductInfo> getProductList = productService.retrieveProductList(List.of(1L, 2L));

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
        ProductCommand.RegisterProduct command = ProductCommand.RegisterProduct.builder()
                .sellerId(1L)
                .name("마스크")
                .price(1000)
                .quantity(10L)
                .build();
        Product stored = command.toEntity();
        when(productStore.store(any(Product.class))).thenReturn(stored);

        ProductInfo productInfo = productService.registerProduct(command);

        assertThat(productInfo.getSellerId()).isEqualTo(1L);
        assertThat(productInfo.getStatus()).isEqualTo(stored.getStatus());
        assertThat(productInfo.getName()).isEqualTo(stored.getName());
        assertThat(productInfo.getPrice()).isEqualTo(stored.getPrice());
    }

    @DisplayName("구매 상품 조회시 구매시 가격을 나타냅니다.")
    @Test
    void retrievePurchasedProducts() {
        int originalPrice = 1000;
        int changePrice = 2000;

        Product product1 = Product.builder()
                .price(originalPrice)
                .name("마스크")
                .sellerId(1L)
                .build();
        product1.reserved();

        Order order = Order.builder()
                .price(originalPrice)
                .productId(1L)
                .buyerId(2L)
                .build();
        order.complete();

        ProductCommand.RetrievePurchaseProduct command = ProductCommand.RetrievePurchaseProduct.builder()
                .buyerId(2L)
                .build();

        product1.changePrice(changePrice);
        List<ProductInfo> mapped = List.of(ProductInfo.builder().product(product1).purchasePrice(originalPrice).build());

        when(orderReader.getOrdersComplete(2L)).thenReturn(List.of(order));
        when(productReader.getProductListByIds(List.of(1L))).thenReturn(List.of(product1));
        when(productInfoMapper.toProductInfos(List.of(product1), List.of(originalPrice))).thenReturn(mapped);

        List<ProductInfo> productInfos = productService.retrievePurchasedProducts(command);

        assertThat(productInfos).hasSize(1)
                .extracting("name", "purchasePrice")
                .containsExactlyInAnyOrder(
                        tuple(product1.getName(), originalPrice)
                );
    }

    @DisplayName("구매자는 예약된 상품을 조회합니다.")
    @Test
    void retrieveReservedProductsByBuyer() {
        int originalPrice = 1000;
        int changePrice = 2000;

        Product product1 = Product.builder()
                .price(originalPrice)
                .name("마스크")
                .sellerId(1L)
                .build();
        Product product2 = Product.builder()
                .price(originalPrice)
                .name("충전기")
                .sellerId(1L)
                .build();

        Order order1 = Order.builder()
                .buyerId(2L)
                .productId(1L)
                .price(originalPrice)
                .build();
        Order order2 = Order.builder()
                .buyerId(2L)
                .productId(2L)
                .price(originalPrice)
                .build();
        order1.reserve();
        order2.reserve();

        RetrieveReservedProductsByBuyer command = RetrieveReservedProductsByBuyer.builder()
                .buyerId(2L)
                .build();

        product1.changePrice(changePrice);
        List<ProductInfo> mapped = List.of(
                ProductInfo.builder().product(product1).purchasePrice(originalPrice).build(),
                ProductInfo.builder().product(product2).purchasePrice(originalPrice).build()
        );

        when(orderReader.getOrdersReserve(2L)).thenReturn(List.of(order1, order2));
        when(productReader.getProductListByIds(List.of(1L, 2L))).thenReturn(List.of(product1, product2));
        when(productInfoMapper.toProductInfos(List.of(product1, product2), List.of(originalPrice, originalPrice)))
                .thenReturn(mapped);

        List<ProductInfo> productInfos = productService.retrieveReservedProductsByBuyer(command);

        assertThat(productInfos).hasSize(2)
                .extracting("name", "purchasePrice")
                .containsExactlyInAnyOrder(
                        tuple(product1.getName(), originalPrice),
                        tuple(product2.getName(), originalPrice)
                );
    }
}
