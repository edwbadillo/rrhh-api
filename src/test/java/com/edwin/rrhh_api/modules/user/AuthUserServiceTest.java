package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.common.exception.EmailAlreadyExistsException;
import com.edwin.rrhh_api.config.security.FirebaseService;
import com.edwin.rrhh_api.modules.user.dto.*;
import com.edwin.rrhh_api.modules.user.email.EmailUpdatedData;
import com.edwin.rrhh_api.modules.user.email.UserCreatedData;
import com.edwin.rrhh_api.modules.user.email.UserEmail;
import com.edwin.rrhh_api.modules.user.exception.UserNotFoundException;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthUserServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthUserMapper authUserMapper;

    @Mock
    private FirebaseService firebaseService;

    @Mock
    private UserEmail userEmail;

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

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest("test@email.com", "Test User");

        when(authUserRepository.existsByEmail(request.email())).thenReturn(false);
        when(firebaseService.userExistsByEmail(request.email())).thenReturn(false);

        UserRecord userRecord = mock(UserRecord.class);
        when(userRecord.getUid()).thenReturn("firebase-uid");

        when(firebaseService.createUser(anyString(), anyString(), anyString()))
                .thenReturn(userRecord);

        when(firebaseService.createEmailVerificationLink(request.email()))
                .thenReturn("https://firebase-link");

        doNothing().when(userEmail).sendCreatedUserEmail(any(UserCreatedData.class));

        AuthUser savedUser = AuthUser.builder()
                .id(UUID.randomUUID())
                .firebaseUid("firebase-uid")
                .email(request.email())
                .fullName(request.fullName())
                .role("RH")
                .isActive(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(authUserRepository.save(any(AuthUser.class))).thenReturn(savedUser);

        AuthUserDetailsResponse expectedResponse = AuthUserDetailsResponse.builder()
                .id(savedUser.getId().toString())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .active(savedUser.isActive())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();

        when(authUserMapper.toDetailsResponse(savedUser)).thenReturn(expectedResponse);

        AuthUserDetailsResponse result = authUserService.createUser(request);

        assertThat(result).isEqualTo(expectedResponse);

        verify(authUserRepository).existsByEmail(request.email());
        verify(firebaseService).userExistsByEmail(request.email());
        verify(firebaseService).createUser(anyString(), anyString(), anyString());
        verify(firebaseService).createEmailVerificationLink(request.email());
        verify(userEmail).sendCreatedUserEmail(any(UserCreatedData.class));
    }

    @Test
    void createUser_shouldThrowIfEmailExistsInDatabase() {
        CreateUserRequest request = new CreateUserRequest("test@email.com", "User");

        when(authUserRepository.existsByEmail(request.email())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> authUserService.createUser(request));
        assertThat(exception.getMessage()).isEqualTo("Email already exists");
        assertThat(exception.getSource()).isEqualTo("db");

        verify(firebaseService, never()).userExistsByEmail(anyString());
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void createUser_shouldThrowIfEmailExistsInFirebase() {
        CreateUserRequest request = new CreateUserRequest("test@email.com", "User");

        when(authUserRepository.existsByEmail(request.email())).thenReturn(false);
        when(firebaseService.userExistsByEmail(request.email())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> authUserService.createUser(request));
        assertThat(exception.getMessage()).isEqualTo("Email already exists");
        assertThat(exception.getSource()).isEqualTo("firebase");

        verify(authUserRepository, never()).save(any());
    }

    @Test
    void createUser_shouldThrowIfFirebaseFails() {
        CreateUserRequest request = new CreateUserRequest("fail@email.com", "User");

        when(authUserRepository.existsByEmail(request.email())).thenReturn(false);
        when(firebaseService.userExistsByEmail(request.email())).thenReturn(false);

        when(firebaseService.createUser(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Firebase error"));

        assertThatThrownBy(() -> authUserService.createUser(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Firebase error");

        verify(authUserRepository, never()).save(any());
    }

    @Test
    void shouldUpdateEmailSuccessfully() {

        UUID userId = UUID.randomUUID();

        // Arrange
        AuthUser user = AuthUser.builder()
                .id(userId)
                .firebaseUid("firebase-uid")
                .email("old@example.com")
                .fullName("Test User")
                .build();

        UserRecord firebaseUser = mock(UserRecord.class);
        when(firebaseUser.getEmail()).thenReturn("old@example.com");

        when(authUserRepository.existsByEmailIgnoreCaseAndIdNot("new@example.com", userId)).thenReturn(false);
        when(authUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(firebaseService.getUserById("firebase-uid")).thenReturn(firebaseUser);
        when(authUserRepository.save(user)).thenReturn(user);
        when(firebaseService.createEmailVerificationLink("new@example.com")).thenReturn("https://link");

        UpdateUserEmailRequest request = new UpdateUserEmailRequest("new@example.com");

        // Act
        UpdateEmailResponse response = authUserService.updateUserEmail(userId, request);

        // Assert
        assertTrue(response.success());
        assertEquals("Correo actualizado correctamente", response.message());
        verify(firebaseService).updateUserEmail("firebase-uid", "new@example.com");
        verify(authUserRepository).save(user);
        verify(userEmail).sendConfirmationEmailUpdated(any(EmailUpdatedData.class));
    }

    @Test
    void shouldReturnNoUpdateIfEmailIsSame() {
        UUID userId = UUID.randomUUID();
        AuthUser user = AuthUser.builder()
                .id(userId)
                .firebaseUid("firebase-uid")
                .email("same@example.com")
                .build();

        UserRecord firebaseUser = mock(UserRecord.class);
        when(firebaseUser.getEmail()).thenReturn("same@example.com");

        when(authUserRepository.existsByEmailIgnoreCaseAndIdNot("same@example.com", userId)).thenReturn(false);
        when(authUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(firebaseService.getUserById("firebase-uid")).thenReturn(firebaseUser);

        UpdateUserEmailRequest request = new UpdateUserEmailRequest("same@example.com");

        UpdateEmailResponse response = authUserService.updateUserEmail(userId, request);

        assertFalse(response.success());
        assertEquals("El correo indicado es el mismo, no se realiza ninguna actualización", response.message());
        verify(firebaseService, never()).updateUserEmail(any(), any());
    }

    @Test
    void shouldThrowIfUserNotFoundInDatabase() {
        UUID userId = UUID.randomUUID();

        when(authUserRepository.existsByEmailIgnoreCaseAndIdNot("new@example.com", userId)).thenReturn(false);
        when(authUserRepository.findById(userId)).thenReturn(Optional.empty());
        UpdateUserEmailRequest request = new UpdateUserEmailRequest("new@example.com");

        assertThrows(UserNotFoundException.class, () ->
                authUserService.updateUserEmail(userId, request)
        );
    }

    @Test
    void shouldThrowIfUserNotFoundInFirebase() {
        UUID userId = UUID.randomUUID();
        AuthUser user = AuthUser.builder()
                .id(userId)
                .firebaseUid("firebase-uid")
                .email("old@example.com")
                .build();

        when(authUserRepository.existsByEmailIgnoreCaseAndIdNot("new@example.com", userId)).thenReturn(false);
        when(authUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(firebaseService.getUserById("firebase-uid")).thenThrow(new UserNotFoundException("Firebase user not found"));

        UpdateUserEmailRequest request = new UpdateUserEmailRequest("new@example.com");

        assertThrows(UserNotFoundException.class, () ->
                authUserService.updateUserEmail(userId, request)
        );
    }

    @Test
    void shouldThrowIfNewEmailAlreadyExistsInFirebase() {
        UUID userId = UUID.randomUUID();
        AuthUser user = AuthUser.builder()
                .id(userId)
                .firebaseUid("firebase-uid")
                .email("old@example.com")
                .build();

        UserRecord firebaseUser = mock(UserRecord.class);
        when(firebaseUser.getEmail()).thenReturn("old@example.com");

        when(authUserRepository.existsByEmailIgnoreCaseAndIdNot("new@example.com", userId)).thenReturn(false);
        when(authUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(firebaseService.getUserById("firebase-uid")).thenReturn(firebaseUser);
        doThrow(new EmailAlreadyExistsException("El nuevo correo ya está registrado en Firebase", "firebase"))
                .when(firebaseService).updateUserEmail("firebase-uid", "new@example.com");

        UpdateUserEmailRequest request = new UpdateUserEmailRequest("new@example.com");

        assertThrows(EmailAlreadyExistsException.class, () ->
                authUserService.updateUserEmail(userId, request)
        );
    }

    @Test
    void updateUserEmail_shouldThrowExceptionIfEmailAlreadyExistsForAnotherUser() {
        UUID userId = UUID.randomUUID();
        String newEmail = "duplicate@example.com";

        when(authUserRepository.existsByEmailIgnoreCaseAndIdNot(newEmail, userId)).thenReturn(true);

        EmailAlreadyExistsException ex = assertThrows(
                EmailAlreadyExistsException.class,
                () -> authUserService.updateUserEmail(userId, new UpdateUserEmailRequest(newEmail))
        );

        assertEquals("Email already exists", ex.getMessage());
        assertEquals("db", ex.getSource());

        verify(authUserRepository, never()).findById(any());
        verify(firebaseService, never()).getUserById(any());
        verify(firebaseService, never()).updateUserEmail(any(), any());
        verify(authUserRepository, never()).save(any());
        verify(userEmail, never()).sendConfirmationEmailUpdated(any());
    }

}
