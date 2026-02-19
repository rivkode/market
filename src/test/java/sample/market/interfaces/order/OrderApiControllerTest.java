package sample.market.interfaces.order;

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
import sample.market.domain.order.Order;
import sample.market.domain.order.OrderCommand;
import sample.market.domain.order.OrderInfo;
import sample.market.interfaces.order.OrderDto.ApproveRequest;
import sample.market.interfaces.order.OrderDto.CompleteRequest;
import sample.market.interfaces.order.OrderDto.RegisterRequest;

class OrderApiControllerTest extends ControllerTestSupport {

    @DisplayName("구매자는 주문 목록을 조회한다.")
    @Test
    void retrieveOrders() throws Exception {
        // given
        Order order = Order.builder()
                .buyerId(2L)
                .productId(1L)
                .price(1000)
                .build();
        OrderInfo mockOrderInfo = new OrderInfo(order);
        when(orderFacade.retrieveOrders(any(OrderCommand.RetrieveOrders.class)))
                .thenReturn(List.of(mockOrderInfo));

        // when // then
        mockMvc.perform(
                        get("/api/v1/orders")
                                .queryParam("buyerId", "2")
                                .queryParam("productId", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderInfos[0].buyerId").value(2))
                .andExpect(jsonPath("$.orderInfos[0].productId").value(1));
    }

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


    @DisplayName("판매자가 구매상품에 대해 판매승인을 한다.")
    @Test
    void approveOrder() throws Exception {
        // given
        long buyerId = 2L;
        long productId = 1L;
        int price = 1000;

        Order order = Order.builder()
                .buyerId(buyerId)
                .productId(productId)
                .price(price)
                .build();

        // 판매 승인
        order.approve();

        OrderDto.ApproveRequest request = ApproveRequest.builder()
                .orderId(1L)
                .productId(productId)
                .sellerId(1L)
                .build();

        OrderInfo mockOrderInfo = new OrderInfo(order);

        when(orderFacade.approveOrder(any(OrderCommand.ApproveOrder.class))).thenReturn(mockOrderInfo);

        // when // then
        mockMvc.perform(
                        post("/api/v1/orders/approve")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ORDER_SALE_APPROVED"))
        ;
    }

    @DisplayName("구매자가 구매상품에 대해 판매승인을 한다.")
    @Test
    void completeOrder() throws Exception {
        // given
        long buyerId = 2L;
        long productId = 1L;
        int price = 1000;

        Order order = Order.builder()
                .buyerId(buyerId)
                .productId(productId)
                .price(price)
                .build();

        // 판매 승인
        order.complete();

        OrderDto.CompleteRequest request = CompleteRequest.builder()
                .orderId(1L)
                .productId(productId)
                .sellerId(1L)
                .build();

        OrderInfo mockOrderInfo = new OrderInfo(order);

        when(orderFacade.completeOrder(any(OrderCommand.CompleteOrder.class))).thenReturn(mockOrderInfo);

        // when // then
        mockMvc.perform(
                        post("/api/v1/orders/complete")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

}
