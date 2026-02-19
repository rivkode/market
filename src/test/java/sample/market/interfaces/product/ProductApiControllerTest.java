package sample.market.interfaces.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import sample.market.ControllerTestSupport;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductCommand;
import sample.market.domain.product.ProductInfo;
import sample.market.domain.product.ProductRetrieveStatus;
import sample.market.interfaces.product.ProductDto.RegisterRequest;

class ProductApiControllerTest extends ControllerTestSupport {


    @DisplayName("신규 상품을 등록한다.")
    @Test
    void registerProduct() throws Exception{
        // given
        ProductDto.RegisterRequest request = RegisterRequest.builder()
                .name("마스크")
                .price(1000)
                .sellerId(1L)
                .build();

        Product product = Product.builder()
                .name("마스크")
                .price(1000)
                .sellerId(1L)
                .build();
        ProductInfo mockProductInfo = new ProductInfo(product);

        when(productFacade.registerProduct(any(ProductCommand.RegisterProduct.class))).thenReturn(mockProductInfo);

        // when // then
        mockMvc.perform(
                        post("/api/v1/products")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("구매자는 구매한 상품을 조회한다. (신규 API)")
    @Test
    void retrievePurchasedProduct() throws Exception{
        // given
        Product product = Product.builder()
                .name("마스크")
                .price(1000)
                .sellerId(1L)
                .build();
        Product product2 = Product.builder()
                .name("마스크")
                .price(1000)
                .sellerId(1L)
                .build();

        ProductInfo mockProductInfo1 = new ProductInfo(product);
        ProductInfo mockProductInfo2 = new ProductInfo(product2);
        List<ProductInfo> productinfos = List.of(mockProductInfo1, mockProductInfo2);


        when(productFacade.retrieveProducts(any(ProductCommand.RetrieveProducts.class))).thenReturn(productinfos);

        // when // then
        mockMvc.perform(
                        get("/api/v1/products")
                                .queryParam("buyerId", "2")
                                .queryParam("status", ProductRetrieveStatus.PURCHASED.name())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productInfos.length()").value(2));
    }

    @DisplayName("신규 조회 API에서 status는 필수값이다.")
    @Test
    void retrieveProductsWithoutStatus() throws Exception{
        // when // then
        mockMvc.perform(
                        get("/api/v1/products")
                                .queryParam("buyerId", "2")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request Error status=null (status는 필수입력값입니다.)"))
        ;
    }
}
