package sample.market.interfaces.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import sample.market.ControllerTestSupport;
import sample.market.application.product.ProductFacade;
import sample.market.application.user.UserFacade;
import sample.market.domain.product.Product;
import sample.market.domain.product.ProductCommand;
import sample.market.domain.product.ProductInfo;
import sample.market.interfaces.product.ProductDto.RegisterRequest;

class ProductApiControllerTest extends ControllerTestSupport {

    @MockBean
    private ProductFacade productFacade;

    @MockBean
    private UserFacade userFacade; // 추가


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

    @DisplayName("get")
    @Test
    void get() throws Exception {
        // given
        String str = "order";

        // when // then
        mockMvc.perform(
                        post("/api/v1/orders")
                                .content(str)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());
    }


}