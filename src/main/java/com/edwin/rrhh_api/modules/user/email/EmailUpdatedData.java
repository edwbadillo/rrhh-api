package com.edwin.rrhh_api.modules.user.email;

import lombok.Builder;

/**
 * Información para el envío de un correo de verificación al destino indicado cuando su correo ha sido actualizado
 *
 * @param email           correo del usuario
 * @param fullName        nombre completo del usuario
 * @param confirmationUrl enlace de verificación generado por Firebase para confirmar el correo
 */
@Builder
public record EmailUpdatedData(
        String email,
        String fullName,
        String confirmationUrl
) {
}
