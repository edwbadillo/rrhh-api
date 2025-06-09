package com.edwin.rrhh_api.modules.user.dto;

import lombok.Builder;

/**
 * DTO con la información de un usuario registrado en base de datos.
 *
 * @param id UUID del usuario en base de datos
 * @param email email del usuario
 * @param fullName nombre completo del usuario
 * @param role rol del usuario
 * @param active estado del usuario
 */
@Builder
public record AuthUserResponse(
        String id,
        String email,
        String fullName,
        String role,
        Boolean active
) {
}
