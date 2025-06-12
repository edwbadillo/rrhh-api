package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.modules.user.dto.AuthUserDetailsResponse;
import com.edwin.rrhh_api.modules.user.dto.AuthUserMapper;
import com.edwin.rrhh_api.modules.user.dto.AuthUserResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthUserMapperTest {
    private final AuthUserMapper mapper = Mappers.getMapper(AuthUserMapper.class);

    @Test
    void shouldConvertAuthUserToAuthUserResponse() {
        AuthUser authUser = AuthUser.builder()
                .id(UUID.randomUUID())
                .email("tGw4U@example.com")
                .fullName("John Doe")
                .firebaseUid("firebase-uid")
                .role("ADMIN")
                .isActive(true)
                .build();

        AuthUserResponse authUserResponse = mapper.toResponse(authUser);

        assertThat(authUserResponse.id()).isEqualTo(authUser.getId().toString());
        assertThat(authUserResponse.email()).isEqualTo(authUser.getEmail());
        assertThat(authUserResponse.fullName()).isEqualTo(authUser.getFullName());
        assertThat(authUserResponse.role()).isEqualTo(authUser.getRole());
        assertThat(authUserResponse.active()).isEqualTo(authUser.isActive());
    }

    @Test
    void shouldConvertAuthUserToAuthUserDetailsResponse() {
        AuthUser authUser = AuthUser.builder()
                .id(UUID.randomUUID())
                .email("tGw4U@example.com")
                .fullName("John Doe")
                .firebaseUid("firebase-uid")
                .role("ADMIN")
                .isActive(true)
                .disabledAt(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        AuthUserDetailsResponse authUserDetailsResponse = mapper.toDetailsResponse(authUser);

        assertThat(authUserDetailsResponse.id()).isEqualTo(authUser.getId().toString());
        assertThat(authUserDetailsResponse.email()).isEqualTo(authUser.getEmail());
        assertThat(authUserDetailsResponse.fullName()).isEqualTo(authUser.getFullName());
        assertThat(authUserDetailsResponse.role()).isEqualTo(authUser.getRole());
        assertThat(authUserDetailsResponse.active()).isEqualTo(authUser.isActive());
        assertThat(authUserDetailsResponse.createdAt()).isEqualTo(authUser.getCreatedAt());
        assertThat(authUserDetailsResponse.updatedAt()).isEqualTo(authUser.getUpdatedAt());
    }
}
