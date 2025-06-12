package com.edwin.rrhh_api.modules.user.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO con toda la información de un usuario registrado en base de datos.
 *
 * @param id         UUID del usuario en base de datos
 * @param email      email del usuario
 * @param fullName   nombre completo del usuario
 * @param role       rol del usuario
 * @param active     estado del usuario
 * @param disabledAt fecha de desactivación del usuario
 * @param createdAt  fecha de creación del usuario
 * @param updatedAt  fecha de actualización del usuario
 */
@Builder
public record AuthUserDetailsResponse(
        String id,
        String email,
        String fullName,
        String role,
        Boolean active,
        LocalDateTime disabledAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
