package com.edwin.rrhh_api.modules.user.email;

/**
 * Define funciones para el envío de emails relacionados con los usuarios.
 */
public interface UserEmail {

    void sendCreatedUserEmail(UserCreatedData userCreatedData);
}
