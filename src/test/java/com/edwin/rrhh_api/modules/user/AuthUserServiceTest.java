package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.modules.user.dto.AuthUserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthUserServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

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

        List<AuthUser> mockUsers = List.of(admin, rh);

        when(authUserRepository.findByRoleIn(List.of("ADMIN", "RH")))
                .thenReturn(mockUsers);

        List<AuthUserInfo> result = authUserService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("role").containsExactlyInAnyOrder("ADMIN", "RH");

        verify(authUserRepository).findByRoleIn(List.of("ADMIN", "RH"));
    }
}
