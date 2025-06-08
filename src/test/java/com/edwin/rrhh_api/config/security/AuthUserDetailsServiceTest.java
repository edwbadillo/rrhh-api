package com.edwin.rrhh_api.config.security;

import com.edwin.rrhh_api.modules.user.AuthUser;
import com.edwin.rrhh_api.modules.user.AuthUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthUserDetailsServiceTest {

    @InjectMocks
    private AuthUserDetailsServiceImpl authUserDetailsService;

    @Mock
    private AuthUserRepository authUserRepository;

    @Test
    void loadUserByFirebaseUidIfUserExists() {
        AuthUser user = AuthUser.builder()
                .id(UUID.randomUUID())
                .firebaseUid("uid123")
                .email("admin@example.com")
                .fullName("John Doe")
                .role("ADMIN")
                .isActive(true)
                .build();

        when(authUserRepository.findByFirebaseUid("uid123")).thenReturn(Optional.of(user));

        AuthUserDetails authUserDetails = authUserDetailsService.loadUserByFirebaseUid("uid123");
        assertEquals(user.getEmail(), authUserDetails.getUsername());
    }

    @Test
    void loadUserByFirebaseUidIfUserDoesNotExist() {
        when(authUserRepository.findByFirebaseUid("uid123")).thenReturn(Optional.empty());

        UsernameNotFoundException exc = assertThrows(
                UsernameNotFoundException.class, () -> authUserDetailsService.loadUserByFirebaseUid("uid123"));

        assertEquals("User not found", exc.getMessage());
    }

    @Test
    void loadUserByFirebaseUidIfUserIsNotActive() {
        AuthUser user = AuthUser.builder()
                .id(UUID.randomUUID())
                .firebaseUid("uid123")
                .email("admin@example.com")
                .fullName("John Doe")
                .role("ADMIN")
                .isActive(false)
                .build();

        when(authUserRepository.findByFirebaseUid("uid123")).thenReturn(Optional.of(user));

        UsernameNotFoundException exc = assertThrows(
                UsernameNotFoundException.class, () -> authUserDetailsService.loadUserByFirebaseUid("uid123"));

        assertEquals("User not active", exc.getMessage());
    }
}
