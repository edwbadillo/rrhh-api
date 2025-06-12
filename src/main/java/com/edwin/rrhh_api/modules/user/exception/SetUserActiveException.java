package com.edwin.rrhh_api.modules.user.exception;

/**
 * Excepción lanzada cuando se intenta desactivar/activar un usuario.
 */
public class SetUserActiveException extends RuntimeException {
    public SetUserActiveException(String message) {
        super(message);
    }
}
