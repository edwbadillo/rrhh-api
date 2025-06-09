package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.config.security.ControllerTest;
import com.edwin.rrhh_api.modules.user.dto.AuthUserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ControllerTest(controllers = AuthUserController.class)
public class AuthUserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthUserService authUserService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldReturnListOfUsersIfAdminAuthenticated() throws Exception {
        List<AuthUserResponse> users = List.of(
                new AuthUserResponse(UUID.randomUUID().toString(), "admin@test.com", "ADMIN USER", "ADMIN", true),
                new AuthUserResponse(UUID.randomUUID().toString(), "rh@test.com", "RH USER", "RH", true)
        );

        given(authUserService.findAll()).willReturn(users);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("ADMIN USER"))
                .andExpect(jsonPath("$[1].role").value("RH"));
    }

    @Test
    @WithMockUser(username = "rh", roles = {"RH"})
    void shouldReturnForbiddenIfRhAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "employee", roles = {"EMPLOYEE"})
    void shouldReturnForbiddenIfEmployeeAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findById_shouldReturnUserResponse() throws Exception {
        UUID userId = UUID.randomUUID();

        AuthUserResponse response = new AuthUserResponse(
                userId.toString(),
                "test@example.com",
                "Test User",
                "ADMIN",
                true
        );

        when(authUserService.findById(userId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.active").value(true));
    }
}
