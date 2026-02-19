package sample.market.interfaces.product;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.market.application.product.ProductFacade;
import sample.market.domain.product.ProductCommand;
import sample.market.domain.product.ProductInfo;
import sample.market.interfaces.product.ProductDto.RegisterResponse;
import sample.market.interfaces.product.ProductDto.RetrieveResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductApiController {

    private final ProductFacade productFacade;

    @PostMapping
    public ResponseEntity<RegisterResponse> registerProduct(@Valid @RequestBody ProductDto.RegisterRequest request) {
        ProductCommand.RegisterProduct command = request.toCommand();
        ProductInfo productInfo = productFacade.registerProduct(command);
        RegisterResponse response = new RegisterResponse(productInfo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/purchase")
    public ResponseEntity<RetrieveResponse> retrievePurchasedProduct(@Valid @RequestBody ProductDto.RetrievePurchasedRequest request) {
        ProductCommand.RetrievePurchaseProduct command = request.toCommand();
        List<ProductInfo> productInfos = productFacade.retrievePurchasedProducts(command);
        RetrieveResponse response = new RetrieveResponse(productInfos);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/reserved/seller")
    public ResponseEntity<RetrieveResponse> retrieveReservedProductBySeller(
            @Valid @RequestBody ProductDto.RetrieveReservedRequestWithSeller request) {
        ProductCommand.RetrieveReservedProductsBySeller command = request.toCommand();
        List<ProductInfo> productInfos = productFacade.retrieveReservedProductsBySeller(command);
        RetrieveResponse response = new RetrieveResponse(productInfos);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/reserved/buyer")
    public ResponseEntity<RetrieveResponse> retrieveReservedProductByBuyer(
            @Valid @RequestBody ProductDto.RetrieveReservedRequestWithBuyer request) {
        ProductCommand.RetrieveReservedProductsByBuyer command = request.toCommand();
        List<ProductInfo> productInfos = productFacade.retrieveReservedProductsByBuyer(command);
        RetrieveResponse response = new RetrieveResponse(productInfos);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<RetrieveResponse> retrieveProductsByStatus(
            @Valid @ModelAttribute ProductDto.RetrieveRequest request) {
        ProductCommand.RetrieveProducts command = request.toRetrieveCommand();
        List<ProductInfo> productInfos = productFacade.retrieveProducts(command);
        RetrieveResponse response = new RetrieveResponse(productInfos);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
