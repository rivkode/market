package sample.market;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import sample.market.application.order.OrderFacade;
import sample.market.application.product.ProductFacade;
import sample.market.application.user.UserFacade;
import sample.market.domain.user.UserService;
import sample.market.interfaces.order.OrderApiController;
import sample.market.interfaces.product.ProductApiController;
import sample.market.interfaces.user.UserApiController;

@WebMvcTest(controllers = {
        UserApiController.class,
        ProductApiController.class,
        OrderApiController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserService userService;

    @MockBean
    protected UserFacade userFacade;

    @MockBean
    protected ProductFacade productFacade;

    @MockBean
    protected OrderFacade orderFacade;


}
