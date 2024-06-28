package sample.market.interfaces.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import sample.market.ControllerTestSupport;
import sample.market.domain.order.Order;
import sample.market.domain.order.OrderCommand;
import sample.market.domain.order.OrderInfo;
import sample.market.interfaces.order.OrderDto.RegisterRequest;

class OrderApiControllerTest extends ControllerTestSupport {

    @DisplayName("구매자는 상품을 구매한다.")
    @Test
    void registerOrder() throws Exception{
        // given
        long buyerId = 2L;
        long productId = 1L;
        int price = 1000;

        Order order = Order.builder()
                .buyerId(buyerId)
                .productId(productId)
                .price(price)
                .build();

        OrderDto.RegisterRequest request = RegisterRequest.builder()
                .buyerId(buyerId)
                .price(price)
                .productId(productId)
                .build();


        OrderInfo mockOrderInfo = new OrderInfo(order);

        when(orderFacade.registerOrder(any(OrderCommand.RegisterOrder.class))).thenReturn(mockOrderInfo);

        // when // then
        mockMvc.perform(
                        post("/api/v1/orders")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
        ;
    }

    @DisplayName("구매자는 상품을 구매할때 상품Id는 필수값이다.")
    @Test
    void registerOrderWithEmptyProductId() throws Exception {
        // given
        long buyerId = 2L;
        long productId = 1L;
        int price = 1000;

        Order order = Order.builder()
                .buyerId(buyerId)
                .productId(productId)
                .price(price)
                .build();

        OrderDto.RegisterRequest request = RegisterRequest.builder()
                .buyerId(buyerId)
                .price(price)
                .productId(null)
                .build();


        OrderInfo mockOrderInfo = new OrderInfo(order);

        when(orderFacade.registerOrder(any(OrderCommand.RegisterOrder.class))).thenReturn(mockOrderInfo);

        // when // then
        mockMvc.perform(
                        post("/api/v1/orders")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request Error productId=null (productId는 필수입력값입니다.)"))
        ;
    }



}