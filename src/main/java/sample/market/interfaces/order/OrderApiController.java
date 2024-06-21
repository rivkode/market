package sample.market.interfaces.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.market.application.order.OrderFacade;
import sample.market.domain.order.OrderCommand;
import sample.market.domain.order.OrderInfo;
import sample.market.interfaces.order.OrderDto.RegisterResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderApiController {

    private final OrderFacade orderFacade;

    @PostMapping
    public ResponseEntity<RegisterResponse> registerOrder(@Valid @RequestBody OrderDto.RegisterRequest request) {
        OrderCommand.RegisterOrder command = request.toCommand();
        OrderInfo orderInfo = orderFacade.registerOrder(command);
        RegisterResponse response = new RegisterResponse(orderInfo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

}
