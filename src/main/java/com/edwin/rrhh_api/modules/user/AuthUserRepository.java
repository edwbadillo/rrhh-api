package com.edwin.rrhh_api.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad {@link AuthUser}, solo obtiene los usuarios
 * registrados en la base de datos PostgreSQL. No obtiene los datos de usuarios
 * registrados en Firebase
 */
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    List<AuthUser> findByRoleIn(List<String> roles);

    Optional<AuthUser> findByFirebaseUid(String firebaseUid);

    boolean existsByEmail(String email);
}
