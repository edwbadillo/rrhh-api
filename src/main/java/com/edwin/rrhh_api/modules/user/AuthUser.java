package com.edwin.rrhh_api.modules.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidad de usuarios, esta es la información adicional que se puede modificar
 * en el sistema local, la autenticación y demás detalles se manejan en Firebase
 * y esta tabla solo guarda la información extra y lo relaciona con un usuario
 * Firebase.
 *
 * Los usuarios administradores deben ser gestionados directamente en Firebase.
 */
@Entity
@Table(name = "auth_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {

    @Id
    @GeneratedValue
    private UUID id;

    // @Column(name = "firebase_uid")
    private String firebaseUid;

    private String email;

    // @Column(name = "full_name")
    private String fullName;

    private String role;

    // @Column(name = "is_active")
    private boolean isActive;

    private OffsetDateTime disabledAt;

    // @Column(name = "created_at")
    private OffsetDateTime createdAt;

    // @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public boolean isAdmin() {
        return Role.ADMIN.toString().equals(role);
    }

    /**
     * Los tipos de usuarios que pueden acceder al sistema
     */
    public enum Role {

        /**
         * Administrador del sistema, puede crear usuarios rh y empleados.
         */
        ADMIN,

        /**
         * Encargado de gestión de RH, no puede crear usuarios.
         */
        RH,

        /**
         * Empleado, solo puede ver sus contratos y pagos.
         */
        EMPLOYEE
    }
}
