package com.edwin.rrhh_api.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO con la información de un usuario a registrar en base de datos.
 *
 * @param email email del usuario
 * @param fullName nombre completo del usuario
 */
public record CreateUserRequest(
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    String email,

    @NotBlank(message = "El nombre es obligatorio")
    String fullName
) {
}
