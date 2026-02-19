package sample.market.interfaces.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.market.application.order.OrderFacade;
import sample.market.domain.order.OrderCommand;
import sample.market.domain.order.OrderInfo;
import sample.market.interfaces.order.OrderDto.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderApiController {

    private final OrderFacade orderFacade;

    @PostMapping
    public ResponseEntity<RegisterResponse> registerOrder(@Valid @RequestBody RegisterRequest request) {
        OrderCommand.RegisterOrder command = request.toCommand();
        OrderInfo orderInfo = orderFacade.registerOrder(command);
        RegisterResponse response = new RegisterResponse(orderInfo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<RetrieveResponse> retrieveOrder(
        @Valid @ModelAttribute RetrieveRequest request
    ) {
        OrderCommand.RetrieveOrders command = request.toCommand();
        List<OrderInfo> orderInfos = orderFacade.retrieveOrders(command);
        RetrieveResponse response = new RetrieveResponse(orderInfos);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/approve")
    public ResponseEntity<ApproveResponse> approveOrder(
            @Valid @RequestBody ApproveRequest request) {
        OrderCommand.ApproveOrder command = request.toCommand();
        OrderInfo orderInfo = orderFacade.approveOrder(command);
        ApproveResponse response = new ApproveResponse(orderInfo);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/complete")
    public ResponseEntity<CompleteResponse> completeOrder(
            @Valid @RequestBody CompleteRequest request) {
        OrderCommand.CompleteOrder command = request.toCommand();
        OrderInfo orderInfo = orderFacade.completeOrder(command);
        CompleteResponse response = new CompleteResponse(orderInfo);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/reserve")
    public ResponseEntity<CompleteResponse> reserveOrder(
            @Valid @RequestBody ReserveRequest request) {
        OrderCommand.ReserveOrder command = request.toCommand();
        OrderInfo orderInfo = orderFacade.reserveOrder(command);
        CompleteResponse response = new CompleteResponse(orderInfo);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
