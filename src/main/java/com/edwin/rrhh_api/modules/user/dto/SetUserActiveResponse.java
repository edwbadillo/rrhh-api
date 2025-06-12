package com.edwin.rrhh_api.modules.user.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

/**
 * DTO con la información del proceso de desactivación/activación de un
 * usuario.
 *
 * @param message       mensaje de respuesta
 * @param currentStatus estado actual del usuario
 * @param disabledAt    fecha de desactivación del usuario si el usuario ha sido desactivado
 */
@Builder
public record SetUserActiveResponse(
        String message,
        boolean currentStatus,
        OffsetDateTime disabledAt
) {
}
