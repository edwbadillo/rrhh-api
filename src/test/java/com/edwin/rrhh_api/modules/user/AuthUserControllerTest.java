package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.config.security.ControllerTest;
import com.edwin.rrhh_api.modules.user.dto.AuthUserDetailsResponse;
import com.edwin.rrhh_api.modules.user.dto.AuthUserResponse;
import com.edwin.rrhh_api.modules.user.dto.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_shouldReturnDetailsResponse_whenRequestIsValid() throws Exception {
        CreateUserRequest request = new CreateUserRequest("newuser@example.com", "New User");

        AuthUserDetailsResponse response = new AuthUserDetailsResponse(
                UUID.randomUUID().toString(),
                "newuser@example.com",
                "New User",
                "RH",
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(authUserService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "email": "newuser@example.com",
                    "fullName": "New User"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.fullName").value("New User"))
                .andExpect(jsonPath("$.role").value("RH"));

        verify(authUserService).createUser(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_shouldReturnBadRequest_whenMissingFields() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "email": ""
                }
            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists()); // Depende del formato de tus errores

        verify(authUserService, never()).createUser(any());
    }


}
