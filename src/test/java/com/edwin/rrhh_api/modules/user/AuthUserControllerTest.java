package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.modules.user.dto.AuthUserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(AuthUserController.class)
public class AuthUserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthUserService authUserService;

    @Test
    void shouldReturnListOfUsers() throws Exception {
        List<AuthUserInfo> users = List.of(
                new AuthUserInfo(UUID.randomUUID().toString(), "admin@test.com", "ADMIN USER", "ADMIN", true),
                new AuthUserInfo(UUID.randomUUID().toString(), "rh@test.com", "RH USER", "RH", true)
        );

        given(authUserService.findAll()).willReturn(users);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("ADMIN USER"))
                .andExpect(jsonPath("$[1].role").value("RH"));
    }
}
