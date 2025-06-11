package com.edwin.rrhh_api.modules.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
public class AuthRepositoryTest {

    @Autowired
    private AuthUserRepository repository;

    @Test
    void findByRoleIn_shouldReturnOnlyMatchingRoles() {
        // Preparar datos
        AuthUser user1 = AuthUser.builder()
                .firebaseUid("uid1")
                .email("admin@test.com")
                .fullName("Admin User")
                .role("ADMIN")
                .isActive(true)
                .createdAt(null)
                .updatedAt(null)
                .build();

        AuthUser user2 = AuthUser.builder()
                .firebaseUid("uid2")
                .email("rh@test.com")
                .fullName("RH User")
                .role("RH")
                .isActive(true)
                .createdAt(null)
                .updatedAt(null)
                .build();

        AuthUser user3 = AuthUser.builder()
                .firebaseUid("uid3")
                .email("employee@test.com")
                .fullName("Employee User")
                .role("EMPLOYEE")
                .isActive(true)
                .createdAt(null)
                .updatedAt(null)
                .build();

        repository.saveAll(List.of(user1, user2, user3));

        // Ejecutar
        List<AuthUser> result = repository.findByRoleIn(List.of("ADMIN", "RH"));

        // Verificar
        assertThat(result)
                .hasSize(2)
                .extracting(AuthUser::getRole)
                .containsExactlyInAnyOrder("ADMIN", "RH");
    }

    @Test
    void findByFirebaseUid_shouldReturnUser() {
        AuthUser user1 = AuthUser.builder()
                .firebaseUid("uid1")
                .email("admin@test.com")
                .fullName("Admin User")
                .role("ADMIN")
                .isActive(true)
                .createdAt(null)
                .updatedAt(null)
                .build();

        repository.save(user1);

        Optional<AuthUser> result = repository.findByFirebaseUid("uid1");

        assertThat(result)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getFirebaseUid()).isEqualTo("uid1")
                );

        Optional<AuthUser> result2 = repository.findByFirebaseUid("uid2");

        assertThat(result2).isEmpty();
    }

    @Test
    void existsByEmail_shouldReturnTrueIfEmailExists() {
        AuthUser user = AuthUser.builder()
                .firebaseUid("uid1")
                .email("test@example.com")
                .fullName("Test User")
                .role("ADMIN")
                .isActive(true)
                .build();

        repository.save(user);

        assertThat(repository.existsByEmail("test@example.com")).isTrue();
        assertThat(repository.existsByEmail("notfound@example.com")).isFalse();
    }

    @Test
    void existsByEmailIgnoreCaseAndIdNot_shouldReturnTrueIfEmailExistsForOtherUser() {
        AuthUser user1 = AuthUser.builder()
                .firebaseUid("uid1")
                .email("duplicate@example.com")
                .fullName("User One")
                .role("ADMIN")
                .isActive(true)
                .build();

        AuthUser user2 = AuthUser.builder()
                .firebaseUid("uid2")
                .email("another@example.com")
                .fullName("User Two")
                .role("ADMIN")
                .isActive(true)
                .build();

        user1 = repository.save(user1);
        user2 = repository.save(user2);

        assertThat(repository.existsByEmailIgnoreCaseAndIdNot("DUPLICATE@example.com", user2.getId()))
                .isTrue();
        assertThat(repository.existsByEmailIgnoreCaseAndIdNot("another@example.com", user2.getId()))
                .isFalse();
    }

}
