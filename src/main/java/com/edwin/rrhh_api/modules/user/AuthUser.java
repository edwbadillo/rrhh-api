package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.common.jpa.AuditableTimestamps;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de usuarios, esta es la información adicional que se puede modificar
 * en el sistema local, la autenticación y demás detalles se manejan en Firebase
 * y esta tabla solo guarda la información extra y lo relaciona con un usuario
 * Firebase.
 * <p>
 * Los usuarios administradores deben ser gestionados directamente en Firebase.
 */
@Entity
@Table(name = "auth_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser extends AuditableTimestamps {

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

    private LocalDateTime disabledAt;

//    @PrePersist
//    public void prePersist() {
//        createdAt = OffsetDateTime.now();
//        updatedAt = createdAt;
//    }
//
//    @PreUpdate
//    public void preUpdate() {
//        updatedAt = OffsetDateTime.now();
//    }

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
