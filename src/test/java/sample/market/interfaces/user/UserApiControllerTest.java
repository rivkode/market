package sample.market.interfaces.user;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import sample.market.ControllerTestSupport;
import sample.market.application.user.UserFacade;
import sample.market.domain.user.User;
import sample.market.domain.user.UserCommand;
import sample.market.domain.user.UserInfo;
import sample.market.interfaces.user.UserDto.RegisterRequest;

class UserApiControllerTest extends ControllerTestSupport {

    @MockBean
    private UserFacade userFacade;


    @DisplayName("신규 유저를 등록한다.")
    @Test
    void registerUser() throws Exception{
        // given
        UserDto.RegisterRequest request = RegisterRequest.builder()
                .email("email@email.com")
                .username("username")
                .password("password")
                .build();

        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("password")
                .build();
        UserInfo mockUserInfo = new UserInfo(user);

        when(userFacade.registerUser(any(UserCommand.class))).thenReturn(mockUserInfo);


        // when // then
        mockMvc.perform(
                        post("/api/v1/users")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(status().isCreated());
    }
}