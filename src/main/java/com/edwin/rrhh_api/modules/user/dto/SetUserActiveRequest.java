package com.edwin.rrhh_api.modules.user.dto;

/**
 * DTO con el valor a establecer para el estado de un usuario.
 *
 * @param isActive estado del usuario a establecer
 */
public record SetUserActiveRequest(
        Boolean isActive
) {
}
