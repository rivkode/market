package sample.market.interfaces.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.market.application.user.UserFacade;
import sample.market.domain.user.UserCommand;
import sample.market.domain.user.UserInfo;
import sample.market.interfaces.user.UserDto.RegisterResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController {

    private final UserFacade userFacade;

    @PostMapping
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody UserDto.RegisterRequest request) {
        UserCommand command = request.toCommand();
        UserInfo userInfo = userFacade.registerUser(command);
        RegisterResponse response = new RegisterResponse(userInfo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);

    }

}
