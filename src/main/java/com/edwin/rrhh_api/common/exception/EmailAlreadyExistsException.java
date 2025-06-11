package com.edwin.rrhh_api.common.exception;

import lombok.Getter;

/**
 * Excepción lanzada cuando se intenta crear un usuario con un correo electrónico
 * que ya existe en Firebase o en la base de datos.
 */
@Getter
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Indica donde existe el correo, si en firebase o db (base de datos).
     */
    private final String source;

    public EmailAlreadyExistsException(String message, String source) {
        super(message);
        this.source = source;
    }
}
