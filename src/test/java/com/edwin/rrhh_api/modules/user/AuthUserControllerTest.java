package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.config.security.ControllerTest;
import com.edwin.rrhh_api.modules.user.dto.*;
import com.edwin.rrhh_api.modules.user.exception.SetUserActiveException;
import com.edwin.rrhh_api.modules.user.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
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

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserEmail_shouldReturnSuccessResponse() throws Exception {
        UUID userId = UUID.randomUUID();
        String newEmail = "new.email@example.com";

        // UpdateUserEmailRequest request = new UpdateUserEmailRequest(newEmail);
        UpdateEmailResponse response = new UpdateEmailResponse("Correo actualizado correctamente", true);

        when(authUserService.updateUserEmail(eq(userId), any(UpdateUserEmailRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "new.email@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Correo actualizado correctamente"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserEmail_shouldReturnBadRequestIfInvalidEmail() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "correo-invalido"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUserEmail_shouldReturnNotFoundIfUserDoesNotExist() throws Exception {
        UUID userId = UUID.randomUUID();

        when(authUserService.updateUserEmail(eq(userId), any(UpdateUserEmailRequest.class)))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "new.email@example.com"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void setUserActive_shouldReturn200_whenSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        SetUserActiveResponse response = new SetUserActiveResponse(
                "Estado actualizado correctamente", false, LocalDateTime.now()
        );

        when(authUserService.setUserActive(eq(id), any(SetUserActiveRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/set-active-status/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "isActive": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value(false))
                .andExpect(jsonPath("$.message").value("Estado actualizado correctamente"))
                .andExpect(jsonPath("$.disabledAt").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void setUserActive_shouldReturn404_whenUserNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(authUserService.setUserActive(eq(id), any(SetUserActiveRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/api/v1/users/set-active-status/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "isActive": false
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void setUserActive_shouldReturn400_whenAdminUser() throws Exception {
        UUID id = UUID.randomUUID();

        when(authUserService.setUserActive(eq(id), any(SetUserActiveRequest.class)))
                .thenThrow(new SetUserActiveException("Un ADMIN no puede ser desactivado"));

        mockMvc.perform(put("/api/v1/users/set-active-status/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "isActive": false
                                }
                                """))
                .andExpect(status().isConflict());
    }

}
