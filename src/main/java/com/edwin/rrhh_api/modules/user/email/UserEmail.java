package com.edwin.rrhh_api.modules.user.email;

/**
 * Define funciones para el envío de emails relacionados con los usuarios.
 */
public interface UserEmail {

    /**
     * Envía un correo de verificación al usuario creado
     *
     * @param userCreatedData datos del usuario creado
     */
    void sendCreatedUserEmail(UserCreatedData userCreatedData);

    /**
     * Envía un correo de verificación al destino indicado cuando su correo ha sido actualizado
     *
     * @param data información para el envío de un correo de verificación
     */
    void sendConfirmationEmailUpdated(EmailUpdatedData data);
}
