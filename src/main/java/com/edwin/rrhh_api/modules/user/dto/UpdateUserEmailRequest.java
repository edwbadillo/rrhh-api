package com.edwin.rrhh_api.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO con la información del correo electrónico de un usuario.
 *
 * @param email nuevo email del usuario
 */
public record UpdateUserEmailRequest(
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El formato del correo electrónico no es válido")
        String email
) {
}
