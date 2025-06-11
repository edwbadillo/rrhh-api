package com.edwin.rrhh_api.modules.user.dto;

/**
 * DTO que indica el resultado de la actualización del correo electrónico de
 * un usuario.
 *
 * @param message mensaje de respuesta
 * @param success indica si la actualización fue exitosa
 */
public record UpdateEmailResponse(
        String message,
        boolean success
) {
}
