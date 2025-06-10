package com.edwin.rrhh_api.modules.user.email;

import com.edwin.rrhh_api.modules.user.AuthUser;
import lombok.Builder;

/**
 * Información para el envío de un correo a un usuario creado.
 *
 * @param user            {@link AuthUser}
 * @param password        contraseña del usuario generada
 * @param confirmationUrl enlace de verificación generado por Firebase para confirmar el correo
 */
@Builder
public record UserCreatedData(
        AuthUser user,
        String password,
        String confirmationUrl
) {
}
