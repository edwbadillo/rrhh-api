package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.modules.user.dto.AuthUserMapper;
import com.edwin.rrhh_api.modules.user.dto.AuthUserResponse;
import com.edwin.rrhh_api.modules.user.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthUserServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthUserMapper authUserMapper;

    @InjectMocks
    private AuthUserServiceImpl authUserService;

    @Test
    void shouldReturnOnlyAdminAndRhUsers() {
        AuthUser admin = AuthUser.builder()
                .id(UUID.randomUUID())
                .firebaseUid("uid1")
                .email("admin@test.com")
                .fullName("Admin User")
                .role("ADMIN")
                .isActive(true)
                .build();

        AuthUser rh = AuthUser.builder()
                .id(UUID.randomUUID())
                .firebaseUid("uid2")
                .email("rh@test.com")
                .fullName("RH User")
                .role("RH")
                .isActive(true)
                .build();

        List<AuthUser> mockUsersDB = List.of(admin, rh);

        when(authUserRepository.findByRoleIn(List.of("ADMIN", "RH")))
                .thenReturn(mockUsersDB);

        AuthUserResponse adminResponse = AuthUserResponse.builder()
                .id(admin.getId().toString())
                .email(admin.getEmail())
                .fullName(admin.getFullName())
                .role(admin.getRole())
                .active(admin.isActive())
                .build();

        AuthUserResponse rhResponse = AuthUserResponse.builder()
                .id(rh.getId().toString())
                .email(rh.getEmail())
                .fullName(rh.getFullName())
                .role(rh.getRole())
                .active(rh.isActive())
                .build();

        when(authUserMapper.toResponse(admin)).thenReturn(adminResponse);
        when(authUserMapper.toResponse(rh)).thenReturn(rhResponse);

        List<AuthUserResponse> result = authUserService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("role").containsExactlyInAnyOrder("ADMIN", "RH");

        verify(authUserRepository).findByRoleIn(List.of("ADMIN", "RH"));
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoUsersFound() {
        // Arrange
        when(authUserRepository.findByRoleIn(List.of("ADMIN", "RH")))
                .thenReturn(List.of());

        // Act
        List<AuthUserResponse> result = authUserService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(authUserRepository).findByRoleIn(List.of("ADMIN", "RH"));
        verifyNoInteractions(authUserMapper);
    }

    @Test
    void findById_shouldReturnUser() {
        // Arrange
        UUID id = UUID.randomUUID();
        AuthUser user = AuthUser.builder()
                .id(id)
                .firebaseUid("uid1")
                .email("admin@test.com")
                .fullName("Admin User")
                .role("ADMIN")
                .isActive(true)
                .build();

        AuthUserResponse authUserResponse = AuthUserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .active(user.isActive())
                .build();

        when(authUserRepository.findById(id)).thenReturn(Optional.of(user));
        when(authUserMapper.toResponse(user)).thenReturn(authUserResponse);

        // Act
        AuthUserResponse result = authUserService.findById(id);

        // Assert
        assertNotNull(result);
        assertThat(result.id()).isEqualTo(id.toString());
        assertThat(result.email()).isEqualTo(user.getEmail());
        assertThat(result.fullName()).isEqualTo(user.getFullName());
        assertThat(result.role()).isEqualTo(user.getRole());
        assertThat(result.active()).isEqualTo(user.isActive());

        verify(authUserRepository).findById(id);
        verify(authUserMapper).toResponse(user);
    }

    @Test
    void findById_shouldThrowUserNotFoundException_whenUserNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(authUserRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authUserService.findById(id));
        assertThat(exception.getMessage()).isEqualTo("User not found");
    }
}
