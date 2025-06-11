package com.edwin.rrhh_api.common.exception;

/**
 * Excepción lanzada cuando se intenta acceder a un recurso
 * que no existe, ya sea un registro de base de datos, archivo,
 * etc.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
